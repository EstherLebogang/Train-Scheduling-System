import java.util.*; 
import java.util.concurrent.*; 
import java.time.LocalTime; 
import java.time.format.DateTimeFormatter; 
public class PrasaTrainSchedulingSystem { 
public static void main(String[] args) { 
SchedulingSystem system = new SchedulingSystem(); 
system.start(); 
    } 
} 
 
class SchedulingSystem { 
    private Scanner scanner = new Scanner(System.in); 
    private TrainScheduler scheduler = new TrainScheduler(); 
    private ExecutorService executorService = Executors.newCachedThreadPool(); 
    private boolean isRunning = true; 
 
    public void start() { 
        System.out.println("===================================================="); 
        System.out.println("  PRASA Train Scheduling System"); 
        System.out.println("===================================================="); 
         
        // Add some initial stations 
        scheduler.addStation("Cape Town", 5); 
        scheduler.addStation("Johannesburg", 8); 
        scheduler.addStation("Durban", 4); 
        scheduler.addStation("Pretoria", 6); 
         
        while (isRunning) { 
            displayMenu(); 
            int choice = getUserChoice(); 
            processChoice(choice); 
        } 
         
        // Shutdown the executor service when the program ends 
        executorService.shutdown(); 
    } 
     
    private void displayMenu() { 
        System.out.println("\n----------------------------------------------------"); 
        System.out.println("Main Menu:"); 
        System.out.println("1. Add Train Schedule"); 
        System.out.println("2. Cancel Train Schedule"); 
        System.out.println("3. View All Train Schedules"); 
        System.out.println("4. View Platform Allocation"); 
        System.out.println("5. Add Station"); 
        System.out.println("6. Run Simulation"); 
        System.out.println("7. Exit"); 
        System.out.println("----------------------------------------------------"); 
        System.out.print("Enter your choice: "); 
    } 
     
    private int getUserChoice() { 
        int choice = -1; 
        try { 
            choice = Integer.parseInt(scanner.nextLine()); 
        } catch (NumberFormatException e) { 
            // Will return -1 which will be handled as invalid choice 
        } 
        return choice; 
    } 
     
    private void processChoice(int choice) { 
        switch (choice) { 
            case 1: 
                addTrainSchedule(); 
                break; 
            case 2: 
                cancelTrainSchedule(); 
                break; 
            case 3: 
                viewTrainSchedules(); 
                break; 
            case 4: 
                viewPlatformAllocation(); 
                break; 
            case 5: 
                addStation(); 
                break; 
            case 6: 
                runSimulation(); 
                break; 
            case 7: 
                isRunning = false; 
                System.out.println("Exiting the PRASA Train Scheduling System. Goodbye!"); 
                break; 
            default: 
                System.out.println("Invalid choice. Please try again."); 
        } 
    } 
     
    private void addTrainSchedule() { 
        System.out.println("\n=== Add Train Schedule ==="); 
         
        System.out.println("Available stations:"); 
        List<String> stations = scheduler.getStationNames(); 
        for (int i = 0; i < stations.size(); i++) { 
            System.out.println((i + 1) + ". " + stations.get(i)); 
        } 
         
        if (stations.isEmpty()) { 
            System.out.println("No stations available. Please add stations first."); 
            return; 
        } 
         
        System.out.print("Select departure station (number): "); 
        int departureIndex = getUserChoice() - 1; 
        if (departureIndex < 0 || departureIndex >= stations.size()) { 
            System.out.println("Invalid station selection."); 
            return; 
        } 
         
        System.out.print("Select arrival station (number): "); 
        int arrivalIndex = getUserChoice() - 1; 
        if (arrivalIndex < 0 || arrivalIndex >= stations.size() || arrivalIndex == departureIndex) { 
            System.out.println("Invalid station selection or same as departure."); 
            return; 
        } 
         
        String departureStation = stations.get(departureIndex); 
        String arrivalStation = stations.get(arrivalIndex); 
         
        System.out.print("Enter train ID: "); 
        String trainId = scanner.nextLine(); 
         
        System.out.print("Enter departure time (HH:MM): "); 
        String departureTimeStr = scanner.nextLine(); 
         
        System.out.print("Enter arrival time (HH:MM): "); 
        String arrivalTimeStr = scanner.nextLine(); 
         
        try { 
            LocalTime departureTime = LocalTime.parse(departureTimeStr, 
DateTimeFormatter.ofPattern("HH:mm")); 
            LocalTime arrivalTime = LocalTime.parse(arrivalTimeStr, 
DateTimeFormatter.ofPattern("HH:mm")); 
             
            boolean scheduled = scheduler.addTrainSchedule(trainId, departureStation, 
arrivalStation, departureTime, arrivalTime); 
             
            if (scheduled) { 
                System.out.println("Train schedule added successfully!"); 
            } else { 
                System.out.println("Failed to add train schedule. Platform conflict or invalid 
timing."); 
            } 
        } catch (Exception e) { 
            System.out.println("Invalid time format. Please use HH:MM format."); 
        } 
    } 
 
 
 
 
     
    private void cancelTrainSchedule() { 
        System.out.println("\n=== Cancel Train Schedule ==="); 
        List<TrainSchedule> schedules = scheduler.getAllTrainSchedules(); 
         
        if (schedules.isEmpty()) { 
            System.out.println("No train schedules to cancel."); 
            return; 
        } 
         
        System.out.println("Current Train Schedules:"); 
        for (int i = 0; i < schedules.size(); i++) { 
            TrainSchedule schedule = schedules.get(i); 
            System.out.printf("%d. Train %s: %s (%s) to %s (%s)\n",  
                i + 1,  
                schedule.getTrainId(),  
                schedule.getDepartureStation(), 
                schedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")), 
                schedule.getArrivalStation(), 
                schedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm"))); 
        } 
         
        System.out.print("Enter schedule number to cancel: "); 
        int scheduleIndex = getUserChoice() - 1; 
         
        if (scheduleIndex < 0 || scheduleIndex >= schedules.size()) { 
            System.out.println("Invalid selection."); 
            return; 
        } 
         
        TrainSchedule scheduleToCancel = schedules.get(scheduleIndex); 
        boolean cancelled = scheduler.cancelTrainSchedule(scheduleToCancel.getTrainId()); 
         
        if (cancelled) { 
            System.out.println("Train schedule cancelled successfully!"); 
        } else { 
            System.out.println("Failed to cancel train schedule."); 
        } 
    } 
 
 
 
     
    private void viewTrainSchedules() { 
        System.out.println("\n=== All Train Schedules ==="); 
        List<TrainSchedule> schedules = scheduler.getAllTrainSchedules(); 
         
        if (schedules.isEmpty()) { 
            System.out.println("No train schedules available."); 
            return; 
        } 
         
        System.out.printf("%-10s %-15s %-15s %-12s %-12s %-10s %-10s\n",  
            "Train ID", "Departure", "Arrival", "Dep. Time", "Arr. Time", "Dep. Plat", "Arr. Plat"); 
        System.out.println("---------------------------------------------------------------------------------------------"); 
         
        for (TrainSchedule schedule : schedules) { 
            System.out.printf("%-10s %-15s %-15s %-12s %-12s %-10d %-10d\n",  
                schedule.getTrainId(), 
                schedule.getDepartureStation(), 
                schedule.getArrivalStation(), 
                schedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")), 
                schedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")), 
                schedule.getDeparturePlatform(), 
                schedule.getArrivalPlatform()); 
        } 
    } 
 
 
 
     
    private void viewPlatformAllocation() { 
        System.out.println("\n=== Platform Allocation ==="); 
        Map<String, Map<Integer, List<TimeSlot>>> platformAllocations = 
scheduler.getPlatformAllocations(); 
         
        if (platformAllocations.isEmpty()) { 
            System.out.println("No platform allocations available."); 
            return; 
        } 
         
        for (String station : platformAllocations.keySet()) { 
            System.out.println("\nStation: " + station); 
            Map<Integer, List<TimeSlot>> platforms = platformAllocations.get(station); 
             
            for (int platform : platforms.keySet()) { 
                System.out.println("  Platform " + platform + ":"); 
                List<TimeSlot> timeSlots = platforms.get(platform); 
                 
                if (timeSlots.isEmpty()) { 
                    System.out.println("    No allocations"); 
                    continue; 
                } 
                 
                timeSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime())); 
                 
                for (TimeSlot slot : timeSlots) { 
                    System.out.printf("    %s - %s: Train %s (%s)\n", 
                        slot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")), 
                        slot.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")), 
                        slot.getTrainId(), 
                        slot.isArrival() ? "Arriving" : "Departing"); 
                } 
            } 
        } 
    } 
 
 
 
 
     
    private void addStation() { 
        System.out.println("\n=== Add Station ==="); 
        System.out.print("Enter station name: "); 
        String stationName = scanner.nextLine(); 
         
        System.out.print("Enter number of platforms: "); 
        int platforms = -1; 
        try { 
            platforms = Integer.parseInt(scanner.nextLine()); 
        } catch (NumberFormatException e) { 
            System.out.println("Invalid number format."); 
            return; 
        } 
         
        if (platforms <= 0) { 
            System.out.println("Number of platforms must be positive."); 
            return; 
        } 
         
        boolean added = scheduler.addStation(stationName, platforms); 
        if (added) { 
            System.out.println("Station added successfully!"); 
        } else { 
            System.out.println("Station already exists or invalid parameters."); 
        } 
    } 
 
 
 
     
    private void runSimulation() { 
        System.out.println("\n=== Running Train Simulation ==="); 
        List<TrainSchedule> schedules = scheduler.getAllTrainSchedules(); 
         
        if (schedules.isEmpty()) { 
            System.out.println("No train schedules to simulate."); 
            return; 
        } 
         
        System.out.println("Starting simulation for all scheduled trains..."); 
         
        // Start each train as a separate thread 
        for (TrainSchedule schedule : schedules) { 
            executorService.submit(new TrainSimulator(schedule)); 
        } 
         
        System.out.println("Simulation started! (Trains will run in the background)"); 
    } 
} 
 
class TrainScheduler { 
    private List<String> stationNames = new ArrayList<>(); 
    private Map<String, Integer> stationPlatforms = new HashMap<>(); 
    private List<TrainSchedule> trainSchedules = new ArrayList<>(); 
     
    // Map of station -> platform -> time slots 
    private Map<String, Map<Integer, List<TimeSlot>>> platformAllocations = new 
HashMap<>(); 
     
    public boolean addStation(String stationName, int platforms) { 
        if (stationName == null || stationName.trim().isEmpty() || platforms <= 0 || 
stationNames.contains(stationName)) { 
            return false; 
        } 
         
        stationNames.add(stationName); 
        stationPlatforms.put(stationName, platforms); 
        platformAllocations.put(stationName, new HashMap<>()); 
         
        for (int i = 1; i <= platforms; i++) { 
            platformAllocations.get(stationName).put(i, new ArrayList<>()); 
        } 
         
        return true; 
    } 
     
    public List<String> getStationNames() { 
        return new ArrayList<>(stationNames); 
    } 
     
    public boolean addTrainSchedule(String trainId, String departureStation, String 
arrivalStation, 
                                    LocalTime departureTime, LocalTime arrivalTime) { 
        if (!validateScheduleInput(trainId, departureStation, arrivalStation, departureTime, 
arrivalTime)) { 
            return false; 
        } 
         
        // Find available departure platform 
        int departurePlatform = findAvailablePlatform(departureStation, departureTime, 
departureTime.plusMinutes(10), trainId); 
        if (departurePlatform == -1) { 
            return false; 
        } 
         
        // Find available arrival platform 
        int arrivalPlatform = findAvailablePlatform(arrivalStation, 
arrivalTime.minusMinutes(10), arrivalTime, trainId); 
        if (arrivalPlatform == -1) { 
            return false; 
        } 
         
        // Create train schedule 
        TrainSchedule schedule = new TrainSchedule( 
            trainId, departureStation, arrivalStation, departureTime, arrivalTime, 
departurePlatform, arrivalPlatform); 
         
        // Allocate departure platform 
        allocatePlatform(departureStation, departurePlatform, departureTime, 
departureTime.plusMinutes(10), trainId, false); 
         
        // Allocate arrival platform 
        allocatePlatform(arrivalStation, arrivalPlatform, arrivalTime.minusMinutes(10), 
arrivalTime, trainId, true); 
         
        trainSchedules.add(schedule); 
        return true; 
    } 
     
    private boolean validateScheduleInput(String trainId, String departureStation, String 
arrivalStation, 
                                         LocalTime departureTime, LocalTime arrivalTime) { 
        // Check if stations exist 
        if (!stationNames.contains(departureStation) || !stationNames.contains(arrivalStation)) 
{ 
            return false; 
        } 
         
        // Check if departure is before arrival 
        if (departureTime.isAfter(arrivalTime) || departureTime.equals(arrivalTime)) { 
            return false; 
        } 
         
        // Check if train ID is unique 
        for (TrainSchedule schedule : trainSchedules) { 
            if (schedule.getTrainId().equals(trainId)) { 
                return false; 
            } 
        } 
         
        return true; 
    } 
     
    private int findAvailablePlatform(String station, LocalTime startTime, LocalTime endTime, 
String trainId) { 
        Map<Integer, List<TimeSlot>> platforms = platformAllocations.get(station); 
        int numPlatforms = stationPlatforms.get(station); 
         
        for (int platform = 1; platform <= numPlatforms; platform++) { 
            List<TimeSlot> timeSlots = platforms.get(platform); 
            boolean available = true; 
             
            for (TimeSlot slot : timeSlots) { 
                // Check if there's an overlap 
                if (!(endTime.isBefore(slot.getStartTime()) || startTime.isAfter(slot.getEndTime()))) { 
                    available = false; 
                    break; 
                } 
            } 
             
            if (available) { 
                return platform; 
            } 
        } 
         
        return -1; // No available platform 
    } 
     
    private void allocatePlatform(String station, int platform, LocalTime startTime, LocalTime 
endTime, String trainId, boolean isArrival) { 
        List<TimeSlot> timeSlots = platformAllocations.get(station).get(platform); 
        timeSlots.add(new TimeSlot(trainId, startTime, endTime, isArrival)); 
    } 
     
    public boolean cancelTrainSchedule(String trainId) { 
        TrainSchedule scheduleToRemove = null; 
         
        for (TrainSchedule schedule : trainSchedules) { 
            if (schedule.getTrainId().equals(trainId)) { 
                scheduleToRemove = schedule; 
                break; 
            } 
        } 
         
        if (scheduleToRemove == null) { 
            return false; 
        } 
         
        // Remove platform allocations 
        removePlatformAllocation(scheduleToRemove.getDepartureStation(), 
scheduleToRemove.getDeparturePlatform(), trainId); 
        removePlatformAllocation(scheduleToRemove.getArrivalStation(), 
scheduleToRemove.getArrivalPlatform(), trainId); 
         
        // Remove schedule 
        trainSchedules.remove(scheduleToRemove); 
         
        return true; 
    } 
     
    private void removePlatformAllocation(String station, int platform, String trainId) { 
        List<TimeSlot> timeSlots = platformAllocations.get(station).get(platform); 
        timeSlots.removeIf(slot -> slot.getTrainId().equals(trainId)); 
    } 
     
    public List<TrainSchedule> getAllTrainSchedules() { 
        return new ArrayList<>(trainSchedules); 
    } 
     
    public Map<String, Map<Integer, List<TimeSlot>>> getPlatformAllocations() { 
        return platformAllocations; 
    } 
} 
 
class TrainSchedule { 
    private String trainId; 
    private String departureStation; 
    private String arrivalStation; 
    private LocalTime departureTime; 
    private LocalTime arrivalTime; 
    private int departurePlatform; 
    private int arrivalPlatform; 
    private TrainStatus status = TrainStatus.SCHEDULED; 
     
    public TrainSchedule(String trainId, String departureStation, String arrivalStation,  
                         LocalTime departureTime, LocalTime arrivalTime,  
                         int departurePlatform, int arrivalPlatform) { 
        this.trainId = trainId; 
        this.departureStation = departureStation; 
        this.arrivalStation = arrivalStation; 
        this.departureTime = departureTime; 
        this.arrivalTime = arrivalTime; 
        this.departurePlatform = departurePlatform; 
        this.arrivalPlatform = arrivalPlatform; 
    } 
     
    public String getTrainId() { 
        return trainId; 
    } 
     
    public String getDepartureStation() { 
        return departureStation; 
    } 
     
    public String getArrivalStation() { 
        return arrivalStation; 
    } 
     
    public LocalTime getDepartureTime() { 
        return departureTime; 
    } 
     
    public LocalTime getArrivalTime() { 
        return arrivalTime; 
    } 
     
    public int getDeparturePlatform() { 
        return departurePlatform; 
    } 
     
    public int getArrivalPlatform() { 
        return arrivalPlatform; 
    } 
     
    public TrainStatus getStatus() { 
        return status; 
    } 
     
    public void setStatus(TrainStatus status) { 
        this.status = status; 
    } 
     
    public long getTravelTimeMinutes() { 
        return departureTime.until(arrivalTime, java.time.temporal.ChronoUnit.MINUTES); 
    } 
} 
 
enum TrainStatus { 
    SCHEDULED, AT_STATION, DEPARTING, IN_TRANSIT, ARRIVING, COMPLETED, 
CANCELLED 
} 
 
class TimeSlot { 
    private String trainId; 
    private LocalTime startTime; 
    private LocalTime endTime; 
    private boolean isArrival; 
     
    public TimeSlot(String trainId, LocalTime startTime, LocalTime endTime, boolean 
isArrival) { 
        this.trainId = trainId; 
        this.startTime = startTime; 
        this.endTime = endTime; 
        this.isArrival = isArrival; 
    } 
     
    public String getTrainId() { 
        return trainId; 
    } 
     
    public LocalTime getStartTime() { 
        return startTime; 
    } 
     
    public LocalTime getEndTime() { 
        return endTime; 
    } 
     
    public boolean isArrival() { 
        return isArrival; 
    } 
} 
 
class TrainSimulator implements Runnable { 
    private TrainSchedule schedule; 
     
    public TrainSimulator(TrainSchedule schedule) { 
        this.schedule = schedule; 
    } 
     
    @Override 
    public void run() { 
        try { 
            System.out.printf("[%s] Train %s simulation started\n", getCurrentTime(), 
schedule.getTrainId()); 
             
            // At departure station 
            schedule.setStatus(TrainStatus.AT_STATION); 
            System.out.printf("[%s] Train %s at %s station (Platform %d)\n",  
                getCurrentTime(), schedule.getTrainId(), schedule.getDepartureStation(), 
schedule.getDeparturePlatform()); 
             
            // Simulate waiting until departure time 
            simulateTimeDelay(1000); 
             
            // Departing 
            schedule.setStatus(TrainStatus.DEPARTING); 
            System.out.printf("[%s] Train %s departing from %s (Platform %d)\n",  
                getCurrentTime(), schedule.getTrainId(), schedule.getDepartureStation(), 
schedule.getDeparturePlatform()); 
             
            // In transit (simulate travel time) 
            schedule.setStatus(TrainStatus.IN_TRANSIT); 
            System.out.printf("[%s] Train %s in transit to %s\n",  
                getCurrentTime(), schedule.getTrainId(), schedule.getArrivalStation()); 
             
            // Simulate travel time (scaled down for demonstration) 
            long travelTime = Math.max(schedule.getTravelTimeMinutes() * 100, 2000); // 
Minimum 2 seconds 
            simulateTimeDelay(travelTime); 
             
            // Arriving 
            schedule.setStatus(TrainStatus.ARRIVING); 
            System.out.printf("[%s] Train %s arriving at %s (Platform %d)\n",  
                getCurrentTime(), schedule.getTrainId(), schedule.getArrivalStation(), 
schedule.getArrivalPlatform()); 
             
            simulateTimeDelay(1000); 
             
            // Completed 
            schedule.setStatus(TrainStatus.COMPLETED); 
            System.out.printf("[%s] Train %s journey completed\n", getCurrentTime(), 
schedule.getTrainId()); 
             
        } catch (InterruptedException e) { 
            schedule.setStatus(TrainStatus.CANCELLED); 
            System.out.printf("[%s] Train %s journey interrupted\n", getCurrentTime(), 
schedule.getTrainId()); 
            Thread.currentThread().interrupt(); 
        } 
    } 
     
    private String getCurrentTime() { 
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); 
    } 
     
private void simulateTimeDelay(long milliseconds) throws InterruptedException { 
Thread.sleep(millisseconds); 
} 
}
