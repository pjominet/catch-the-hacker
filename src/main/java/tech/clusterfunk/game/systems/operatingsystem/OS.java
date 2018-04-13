package tech.clusterfunk.game.systems.operatingsystem;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.game.systems.filesystem.NodeType;
import tech.clusterfunk.game.systems.network.Network;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.FilesystemLoader;
import tech.clusterfunk.util.exceptions.InvalidIPException;
import tech.clusterfunk.util.exceptions.UnknownIPException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.SUDO;

public class OS {
    private String name;
    private Map<String, Command> commandSet;
    private Node currentFSPosition;
    private Node fsRoot;
    private String user;
    private int accessLevel;

    private void loadFS(String osName) {
        fsRoot = FilesystemLoader.parseFileSystem(osName);
    }

    private void loadCS(String osName) {
        List<Command> set = CommandLoader.loadCommandSet(osName);
        commandSet = new ConcurrentHashMap<>();
        set.forEach(command -> commandSet.put(command.getName(), command));
    }

    public OS(String name, String user, int accessLevel) {
        this.name = name;
        this.user = user;
        this.accessLevel = accessLevel;
        loadCS(this.name);
        loadFS(this.name);
        this.currentFSPosition = this.fsRoot;
        setCorrectUserHomeFolder(this.currentFSPosition);
    }

    /**
     * Replace Generic user from config with actual user
     *
     * @param current recursion iteration
     */
    private void setCorrectUserHomeFolder(Node current) {
        if (current.getName().equals("NewUser")) current.setName(user);
        else current.getChildren().forEach(this::setCorrectUserHomeFolder);
    }

    public String getName() {
        return name;
    }

    public Node getFsRoot() {
        return fsRoot;
    }

    public Node getCurrentFSPosition() {
        return currentFSPosition;
    }

    public String getUser() {
        return user;
    }

    public boolean hasCommand(String cmdName) {
        return commandSet.get(cmdName) != null;
    }

    public Command getFromCommandSet(String cmdName) {
        return commandSet.get(cmdName);
    }

    /**
     * Traverse filesystem tree upwards
     *
     * @return List
     */
    private List<String> getDirectoriesToRoot() {
        Node current = currentFSPosition;
        List<String> directoryNames = new ArrayList<>();
        while (current.getParent() != null) {
            directoryNames.add(current.getName());
            current = current.getParent();
        }
        directoryNames.add(current.getName());

        Collections.reverse(directoryNames);
        return directoryNames;
    }

    /**
     * build a path to the current filesystem position
     *
     * @return String
     */
    public String getCurrentPath() {
        StringBuilder builder = new StringBuilder();
        getDirectoriesToRoot().forEach(name -> {
            builder.append(name);
            if (!name.equals("/"))
                builder.append("/");
        });

        return builder.toString();
    }

    // Permission control

    /**
     * Checks if a node can be manipulated
     *
     * @param node        to be manipulated
     * @param permission  to check for
     * @param accessLevel to identify privilege level
     * @return boolean
     */
    private boolean isPermitted(Node node, char permission, int accessLevel) {
        if (hasPrivilege(accessLevel)) {
            if (node.hasPermission(permission)) return true;
            else {
                System.err.println(node.getName() + ": Permission denied");
                return false;
            }
        } else {
            System.err.println(node.getName() + ": Not high enough privilege");
            return false;
        }
    }

    /**
     * Checks if a node is accessible
     *
     * @param accessLevel to check for
     * @return boolean
     */
    private boolean hasPrivilege(int accessLevel) {
        if (accessLevel == SUDO) return true;
        else return accessLevel >= this.accessLevel;
    }

    // TODO: implement proper check (similar to hack method)
    public boolean isSudoAllowed(String password) {
        return password.equals("root");
    }

    // helper methods

    /**
     * changes a permission on a given node
     *
     * @param modifier   that determines if to add or subtract a permission
     * @param permission in question
     * @param node       to have its permission changed
     */
    private void changePermission(char modifier, char permission, Node node) {
        char[] replacer = node.getPermissions();
        if (modifier == '-') {
            switch (permission) {
                case 'r':
                    replacer[0] = '-';
                    break;
                case 'w':
                    replacer[1] = '-';
                    break;
                case 'x':
                    replacer[2] = '-';
                    break;
                default:
                    break;
            }
        } else if (modifier == '+') {
            switch (permission) {
                case 'r':
                    replacer[0] = permission;
                    break;
                case 'w':
                    replacer[1] = permission;
                    break;
                case 'x':
                    replacer[2] = permission;
                    break;
                default:
                    break;
            }
        }
        node.setPermissions(replacer);
    }

    /**
     * Creates a new node in the filesystem tree
     *
     * @param name        of the new node
     * @param type        of the new node
     * @param accessLevel to check for
     * @return new node
     */
    private Node createNode(String name, Node parent, NodeType type, int accessLevel) {
        if (isPermitted(parent, 'w', accessLevel)) {
            return new Node(parent, name, type, parent.getPermissions());
        } else return null;
    }

    // Commands

    /**
     * allows to change the current position in the filesystem tree (cd)
     *
     * @param name        of destination directory
     * @param current     recursion iteration
     * @param accessLevel to check for
     */
    public void changeDirectory(String name, Node current, int accessLevel) {
        if (current.getType() == NodeType.DIRECTORY) {
            if (isPermitted(current, 'x', accessLevel)) {
                if (name.equals("..")) currentFSPosition = current.getParent();
                else if (current.getName().equals(name) ||
                        (name.equals("~") && current.getName().equals(user)))
                    currentFSPosition = current;
                else current.getChildren().forEach(child -> changeDirectory(name, child, accessLevel));
            }
        } else System.err.println(name + " is no directory.");
    }

    /**
     * Ping a given address in the network (ping)
     *
     * @param network in question
     * @param ip      to ping
     */
    public void ping(Network network, String ip) {
        if (ip.equals("127.0.0.1"))
            System.err.println("Pinging your own system is somewhat pointless, don't you think?");
        else {
            try {
                if (network.isComputerAt(ip)) {
                    System.out.println("Pinging " + ip + " with 32 bytes of data:");
                    for (int i = 0; i < 4; i++) {
                        System.out.println("\tReply from " + ip + ": bytes=32 time<1ms TTL=64");
                        // wait for show effect
                        Thread.sleep(777);
                    }
                    System.out.println();
                    System.out.println("Ping statistics for " + ip + ":");
                    System.out.println("\tPackets: Sent = 4, Received = 4, Lost = 0 (0% loss)");
                }
            } catch (InvalidIPException | UnknownIPException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * List all direct child node of the current filesystem position (ls)
     *
     * @param accessLevel to check for
     */
    public void list(int accessLevel) {
        if (isPermitted(currentFSPosition, 'r', accessLevel)) {
            currentFSPosition.getChildren().forEach(child ->
                    System.out.format("%s %s " + child.getName() + "%n",
                            child.getType().getAbbreviation(),
                            String.valueOf(child.getPermissions()))
            );
        }
    }

    /**
     * Change permission modes of a given node (chmod)
     *
     * @param modeChange  param required from command
     * @param file        param required from command
     * @param current     recursion iteration
     * @param accessLevel to check for
     */
    public void changeMode(String modeChange, String file, Node current, int accessLevel) {
        if (hasPrivilege(accessLevel)) {
            if (current.getName().equals(file)) {
                char modifier = modeChange.charAt(0);
                String permissions = modeChange.substring(1);
                for (int i = 0; i < permissions.length(); i++) {
                    changePermission(modifier, permissions.charAt(i), current);
                }
            } else current.getChildren().forEach(child -> changeMode(modeChange, file, child, accessLevel));
        }
    }

    /**
     * Read and return contents of a file (vim)
     *
     * @param name        of the file
     * @param accessLevel to check for
     * @return String
     */
    public String readFromFile(String name, int accessLevel) {
        String out = "";
        Node file = currentFSPosition.findChildBy(name);
        if (file != null) {
            if (isPermitted(file, 'r', accessLevel)) {
                if (file.getType() == NodeType.FILE)
                    out = file.getContent();
                else System.err.println(name + " is no file");
            }
        } else System.err.println(name + "does not exist");
        return out;
    }

    /**
     * Write content to a file, if the file does not exist, a new one is created (echo)
     *
     * @param content     to write to the file
     * @param name        of the file
     * @param accessLevel to check for
     */
    public void writeToFile(String content, String name, int accessLevel) {
        Node file = currentFSPosition.findChildBy(name);
        if (file != null) {
            if (isPermitted(file, 'w', accessLevel)) {
                if (file.getType() == NodeType.FILE)
                    file.setContent(content);
                else System.err.println(name + " is no file");
            }
        } else {
            if (isPermitted(currentFSPosition, 'w', accessLevel)) {
                file = createNode(name, currentFSPosition, NodeType.FILE, accessLevel);
                if (file != null) {
                    file.setContent(content);
                    currentFSPosition.getChildren().add(file);
                } else System.err.println("Could not create new file");
            }
        }
    }

    /**
     * Remove a child node from current node in filesystem tree (rm)
     *
     * @param name        of the node to remove
     * @param accessLevel to check for
     */
    public void remove(String name, int accessLevel) {
        if (currentFSPosition == fsRoot)
            System.out.println("You cannot delete root nor its children, stop trying to destroy the system!");
        else {
            Node node = currentFSPosition.findChildBy(name);
            if (node != null) {
                if (isPermitted(node, 'x', accessLevel))
                    currentFSPosition.getChildren().remove(node);
            } else System.err.println("No such file or directory: " + name);
        }
    }

    /**
     * Copy a source node to a destination node (cp)
     * If the source node has the same name as the destination node then add "copy-" prefix
     *
     * @param source      node to copy
     * @param destination parent to copy to
     * @param accessLevel to check for
     */
    public void copy(String source, String destination, int accessLevel) {
        Node sourceNode = currentFSPosition.findChildBy(source);
        if (isPermitted(sourceNode, 'r', accessLevel)) {
            Node destinationNode = currentFSPosition;
            if (!destination.equals("./")) destinationNode = fsRoot.findChildBy(destination);
            if (destinationNode.getType() == NodeType.DIRECTORY) {
                if (isPermitted(destinationNode, 'w', accessLevel)) {
                    if (destinationNode.getChildren().contains(sourceNode)) {
                        source = "copy-" + source;
                    }
                    Node copy = createNode(source, sourceNode.getParent(), sourceNode.getType(), accessLevel);
                    destinationNode.getChildren().add(copy);
                }
            } else System.err.println("Destination is no directory");
        }
    }
}
