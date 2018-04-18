package tech.clusterfunk.game.systems.operatingsystem;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.game.systems.filesystem.NodeType;
import tech.clusterfunk.game.systems.network.Network;
import tech.clusterfunk.util.ConfigLoader;
import tech.clusterfunk.util.FilesystemLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.*;

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
        List<Command> set = ConfigLoader.loadCommandSet(osName);
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
    public String getCurrentPrompt() {
        StringBuilder builder = new StringBuilder();
        getDirectoriesToRoot().forEach(name -> {
            builder.append(name);
            if (!name.equals("/"))
                builder.append("/");
        });
        builder.append(" > ");

        return builder.toString();
    }

    // Permission control

    /**
     * Checks if a node allows be manipulated
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
                err.println(node.getName() + ": Permission denied");
                return false;
            }
        } else {
            err.println(node.getName() + ": Not high enough privilege");
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

    /**
     * Isolate the terminal node from a path string
     *
     * @param path to manipulate
     * @return String
     */
    private String getPathTerminus(String path) {
        if (path.contains("/"))
            return path.substring(path.lastIndexOf("/") + 1);
        else return path;
    }

    /**
     * Retrieve the parent node of the terminal path node
     *
     * @param path to manipulate
     * @return String
     */
    private String cutPathTerminus(String path) {
        if (path.contains("/"))
            return path.substring(0, path.lastIndexOf("/"));
        else return "./";
    }

    /**
     * Find a node by its name
     *
     * @param name    of the node to find
     * @param current recursion iteration
     * @return Node
     */
    private Node findNodeBy(String name, Node current) {
        if (current.getName().equals(name)) return current;
        else {
            for (Node child : current.getChildren()) {
                Node found = findNodeBy(name, child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Commands

    /**
     * Allows to change the current position in the filesystem tree (cd)
     *
     * @param path        to destination directory
     * @param current     recursion iteration
     * @param accessLevel to check for
     */
    public void changeDirectory(String path, Node current, int accessLevel) {
        if (current.getType() == NodeType.DIRECTORY) {
            if (isPermitted(current, 'x', accessLevel)) {
                if (path.equals("..")) currentFSPosition = current.getParent();
                else if ((path.equals("~") && current.getName().equals(user)) ||
                        current.getName().equals(getPathTerminus(path)))
                    currentFSPosition = current;
                else current.getChildren().forEach(child -> changeDirectory(path, child, accessLevel));
            }
        } else err.println(path + " is no valid path.");
    }

    /**
     * Ping a given address in the network (ping)
     *
     * @param network in question
     * @param ip      to ping
     */
    public void ping(Network network, String ip) {
        if (ip.equals("127.0.0.1"))
            err.println("Pinging your own system is somewhat pointless, don't you think?");
        else {
            try {
                if (network.isComputerAt(ip)) {
                    out.println("Pinging " + ip + " with 32 bytes of data:");
                    for (int i = 0; i < 4; i++) {
                        out.println("\tReply from " + ip + ": bytes=32 time<1ms TTL=64");
                        // wait for show effect
                        Thread.sleep(777);
                    }
                    out.println();
                    out.println("Ping statistics for " + ip + ":");
                    out.println("\tPackets: Sent = 4, Received = 4, Lost = 0 (0% loss)");
                } else err.println("Request timed out\n\tNo reachable machine at " + ip);
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
                    out.format("%s %s " + child.getName() + "%n",
                            child.getType().getAbbreviation(),
                            String.valueOf(child.getPermissions()))
            );
        }
    }

    /**
     * Change permission modes of a given node (chmod)
     *
     * @param modeChange  param required from command
     * @param path        param required from command
     * @param current     recursion iteration
     * @param accessLevel to check for
     */
    public void changeMode(String modeChange, String path, Node current, int accessLevel) {
        if (hasPrivilege(accessLevel)) {
            if (current.getName().equals(getPathTerminus(path)) || path.equals("./")) {
                char modifier = modeChange.charAt(0);
                String permissions = modeChange.substring(1);
                for (int i = 0; i < permissions.length(); i++) {
                    changePermission(modifier, permissions.charAt(i), current);
                }
            } else current.getChildren().forEach(child -> changeMode(modeChange, path, child, accessLevel));
        }
    }

    /**
     * Read and return contents of a file (vim)
     *
     * @param path        to the file
     * @param accessLevel to check for
     */
    public void readFromFile(String path, int accessLevel) {
        String fileName = getPathTerminus(path);
        Node file = findNodeBy(fileName, fsRoot);
        if (file != null) {
            if (isPermitted(file, 'r', accessLevel)) {
                if (file.getType() == NodeType.FILE)
                    out.print(file.getContent());
                else err.println(fileName + " is no file");
            }
        } else err.println(fileName + " does not exist");
    }

    /**
     * Write content to a file, if the file does not exist, a new one is created (echo)
     *
     * @param content     to write to the file
     * @param path        to the file
     * @param accessLevel to check for
     */
    public void writeToFile(String content, String path, int accessLevel) {
        String fileName = getPathTerminus(path);
        Node file = findNodeBy(fileName, fsRoot);
        if (file != null) {
            if (file.getType() == NodeType.FILE) {
                if (isPermitted(file, 'w', accessLevel))
                    file.setContent(content);
            } else err.println(fileName + " is no file");
        } else {
            String parentPath = cutPathTerminus(path);
            Node parent = currentFSPosition;
            if (!parentPath.equals("./")) parent = findNodeBy(parentPath, fsRoot);
            if (parent != null) {
                if (isPermitted(parent, 'w', accessLevel)) {
                    file = createNode(fileName, parent, NodeType.FILE, accessLevel);
                    if (file != null) {
                        file.setContent(content);
                        parent.getChildren().add(file);
                    } else err.println("Could not create new file");
                }
            } else err.println(path + " is no valid path");
        }
    }

    /**
     * Remove a child node from current node in filesystem tree (rm)
     *
     * @param path        to the node to remove
     * @param accessLevel to check for
     */
    public void remove(String path, int accessLevel) {
        Node node = findNodeBy(getPathTerminus(path), fsRoot);
        if (node == fsRoot)
            err.println("You cannot delete root nor its children, stop trying to destroy the system!");
        else if (node != null) {
            if (!node.getChildren().isEmpty()) {
                err.println(path + " is not empty");
            } else {
                if (isPermitted(node, 'x', accessLevel))
                    node.getParent().getChildren().remove(node);
                else err.println("No such file or directory: " + path);
            }
        }
    }

    /**
     * Copy a source node to a destination node (cp)
     * If the source node has the same name as the destination node then add "copy-" prefix
     *
     * @param sourcePath      node to copy
     * @param destinationPath parent to copy to
     * @param accessLevel     to check for
     */
    public void copy(String sourcePath, String destinationPath, int accessLevel) {
        String sourceName = getPathTerminus(sourcePath);
        Node source = findNodeBy(sourceName, fsRoot);
        if (source != null) {
            if (isPermitted(source, 'r', accessLevel)) {
                Node destination = currentFSPosition;
                if (!destinationPath.equals("./")) destination = findNodeBy(getPathTerminus(destinationPath), fsRoot);
                if (destination != null) {
                    if (destination.getType() == NodeType.DIRECTORY) {
                        if (isPermitted(destination, 'w', accessLevel)) {
                            if (destination.getChildren().contains(source)) {
                                sourceName = "copy-" + sourceName;
                            }
                            Node copy = createNode(sourceName, source.getParent(), source.getType(), accessLevel);
                            destination.getChildren().add(copy);
                        }
                    } else err.println("Destination is no directory");
                }
            }
        } else err.println(sourcePath + " does not exist");
    }

    public void networkMap(Network network) {
        out.print(network.toString());
    }
}
