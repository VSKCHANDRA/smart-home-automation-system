import java.util.*;
import java.text.SimpleDateFormat;

class Device {
    String name;
    boolean status; // true for ON, false for OFF

    Device(String name) {
        this.name = name;
        this.status = false;
    }

    void toggleStatus() {
        this.status = !this.status;
    }

    String getStatus() {
        return this.status ? "ON" : "OFF";
    }
}

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class Action {
    String action;
    String deviceName;
    Date timestamp;

    Action(String action, String deviceName, Date timestamp) {
        this.action = action;
        this.deviceName = deviceName;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return "[" + sdf.format(timestamp) + "] " + action + " " + deviceName;
    }
}

class SmartHome {
    Map<String, User> users = new HashMap<>();
    Map<String, Device> devices = new HashMap<>();
    List<Action> actionHistory = new ArrayList<>();
    Map<String, Set<String>> deviceGroups = new HashMap<>();
    Map<String, Timer> deviceTimers = new HashMap<>();

    void registerUser(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;
        }
        if (users.containsKey(username)) {
            System.out.println("User already registered.");
            return;
        }
        User user = new User(username, password);
        users.put(username, user);
        System.out.println("User registered successfully.");
    }

    User loginUser(String username, String password) {
        if (users.containsKey(username) && users.get(username).password.equals(password)) {
            return users.get(username);
        }
        System.out.println("Invalid username or password.");
        return null;
    }

    void addDevice(String deviceName) {
        if (devices.containsKey(deviceName)) {
            System.out.println("Device already exists.");
            return;
        }
        devices.put(deviceName, new Device(deviceName));
        System.out.println("Device added successfully.");
    }

    void toggleDevice(String deviceName) {
        if (devices.containsKey(deviceName)) {
            devices.get(deviceName).toggleStatus();
            actionHistory.add(new Action("Toggled", deviceName, new Date()));
            System.out.println(deviceName + " is now " + devices.get(deviceName).getStatus());
        } else {
            System.out.println("Device not found.");
        }
    }

    void showDevices() {
        for (Device device : devices.values()) {
            System.out.println(device.name + ": " + device.getStatus());
        }
    }

    void showActionHistory() {
        for (Action action : actionHistory) {
            System.out.println(action);
        }
    }

    void groupDevices(String groupName, List<String> deviceNames) {
        deviceGroups.putIfAbsent(groupName, new HashSet<>());
        Set<String> group = deviceGroups.get(groupName);
        for (String deviceName : deviceNames) {
            if (devices.containsKey(deviceName)) {
                group.add(deviceName);
            } else {
                System.out.println("Device " + deviceName + " not found.");
            }
        }
        System.out.println("Devices added to group " + groupName + " successfully.");
    }

    void toggleGroup(String groupName) {
        if (deviceGroups.containsKey(groupName)) {
            Set<String> group = deviceGroups.get(groupName);
            for (String deviceName : group) {
                toggleDevice(deviceName);
            }
        } else {
            System.out.println("Group not found.");
        }
    }

    void scheduleDevice(String deviceName, Date date) {
        if (devices.containsKey(deviceName)) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    toggleDevice(deviceName);
                }
            };
            timer.schedule(task, date);
            deviceTimers.put(deviceName, timer);
            System.out.println("Device " + deviceName + " scheduled to toggle on " + new SimpleDateFormat("dd-MM-yyyy").format(date));
        } else {
            System.out.println("Device not found.");
        }
    }
}

public class SmartHomeAutomationSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartHome smartHome = new SmartHome();

        User loggedInUser = null;

        // Registration
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();
        smartHome.registerUser(username, password, confirmPassword);

        // Login
        while (loggedInUser == null) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            loggedInUser = smartHome.loginUser(username, password);
        }

        // Adding devices
        System.out.print("Enter the number of devices to add: ");
        int numDevices = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 0; i < numDevices; i++) {
            System.out.print("Enter device name: ");
            String deviceName = scanner.nextLine();
            smartHome.addDevice(deviceName);
        }

        // User menu for managing devices
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Toggle Device");
            System.out.println("2. Show Devices");
            System.out.println("3. Show Action History");
            System.out.println("4. Group Devices");
            System.out.println("5. Toggle Group");
            System.out.println("6. Schedule Device");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter device name to toggle: ");
                    String deviceName = scanner.nextLine();
                    smartHome.toggleDevice(deviceName);
                    break;
                case 2:
                    smartHome.showDevices();
                    break;
                case 3:
                    smartHome.showActionHistory();
                    break;
                case 4:
                    System.out.print("Enter group name: ");
                    String groupName = scanner.nextLine();
                    System.out.print("Enter device names separated by commas: ");
                    String[] deviceNames = scanner.nextLine().split(",");
                    List<String> deviceList = Arrays.asList(deviceNames);
                    smartHome.groupDevices(groupName, deviceList);
                    break;
                case 5:
                    System.out.print("Enter group name to toggle: ");
                    groupName = scanner.nextLine();
                    smartHome.toggleGroup(groupName);
                    break;
                case 6:
                    System.out.print("Enter device name to schedule: ");
                    deviceName = scanner.nextLine();
                    System.out.print("Enter date (dd-MM-yyyy): ");
                    String dateStr = scanner.nextLine();
                    try {
                        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                        smartHome.scheduleDevice(deviceName, date);
                    } catch (Exception e) {
                        System.out.println("Invalid date format.");
                    }
                    break;
                case 7:
                    exit = true;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        scanner.close();
    }
}