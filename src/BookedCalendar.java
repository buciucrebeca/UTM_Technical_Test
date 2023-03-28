import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookedCalendar {
    public static List<String[]> findAvailableTimes(List<String[]> calendar1, String[] rageLimits1,
                                                    List<String[]> calendar2, String[] rageLimits2, int meeting){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        // Convert range limits to LocalTime
        LocalTime start1 = LocalTime.parse(rageLimits1[0], formatter);
        LocalTime end1 = LocalTime.parse(rageLimits1[1], formatter);
        LocalTime start2 = LocalTime.parse(rageLimits2[0], formatter);
        LocalTime end2 = LocalTime.parse(rageLimits2[1], formatter);

        // Initialize the current time pointer for each calendar to their start time
        LocalTime currentTime1 = start1;
        LocalTime currentTime2 = start2;

        // Merge the two calendars into a single lift of events
        List<String[]> events = new ArrayList<>(calendar1);
        events.addAll(calendar2);

        // Sort the events by start time
        events.sort((a , b) -> LocalTime.parse(a[0], formatter).compareTo(LocalTime.parse(b[0], formatter)));

        List<String[]> availableTimes = new ArrayList<>();

        // Check each event and find the gaps between them
        for(String[] event: events) {
            LocalTime start = LocalTime.parse(event[0], formatter);
            LocalTime end = LocalTime.parse(event[1], formatter);

            // Check if there is a gap between the end of the previous event and the start of this event
            if (currentTime1.isBefore(start) && currentTime2.isBefore(start)) {
                // Calculate the duration of the gap
                int duration = (int) currentTime1.until(start, ChronoUnit.MINUTES);
                // Check if the duration is equal to or greater than the meeting time
                if (duration >= meeting) {
                    // Add the gap to the list of available time slots
                    availableTimes.add(new String[] {currentTime1.toString(), start.toString()});
                }
            }

            // Update the current time pointer for the calendar that has the earlier end time
            if (end.isBefore(end1) || end.equals(end1)) {
                currentTime1 = end;
            }

            if (end.isBefore(end2) || end.equals(end2)) {
                currentTime2 = end;
            }
        }

        // Check if there is a gap between the end of the last event and the end of the calendar ranges
        if (currentTime1.isBefore(end1) && currentTime2.isBefore(end2)) {
            if (currentTime1.isBefore(end2) && currentTime2.isBefore(end1)) {
                int duration = (int) currentTime1.until(end1, ChronoUnit.MINUTES);
                if (duration >= meeting) {
                    int val = end1.compareTo(end2);
                    LocalTime max;
                    if (val >= 0) {
                        max = end2;
                    } else {
                        max = end1;
                    }
                    availableTimes.add(new String[] {currentTime1.toString(), max.toString()});
                }
            }

        }

        return  availableTimes;
    }

    public static void main(String[] args) {
        List<String[]> calendar1 = new ArrayList<>();
        calendar1.add(new String[] {"9:00", "10:30" });
        calendar1.add(new String[] {"12:00", "13:00" });
        calendar1.add(new String[] {"16:00", "18:00" });
        String[] rageLimits1 = {"9:00", "20:00" };

        List<String[]> calendar2 = new ArrayList<>();
        calendar2.add(new String[] {"10:00", "11:30" });
        calendar2.add(new String[] {"12:30", "14:30" });
        calendar2.add(new String[] {"14:30", "15:00" });
        String[] rageLimits2 = {"10:00", "18:30"};

        int meeting = 30;

        List<String[]> availableTime = findAvailableTimes(calendar1, rageLimits1,calendar2,rageLimits2,meeting);

        //print available time

        for( String[] time : availableTime){
            System.out.println(time[0] + " - " + time[1]);
        }

    }


}