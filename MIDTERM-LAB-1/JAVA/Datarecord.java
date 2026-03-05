// DataRecord.java
// Represents a single video game sales record from the dataset

public class Datarecord {
    private String title;
    private String console;
    private String genre;
    private String publisher;
    private double totalSales;
    private String releaseDate;

    public Datarecord(String title, String console, String genre,
                      String publisher, double totalSales, String releaseDate) {
        this.title = title;
        this.console = console;
        this.genre = genre;
        this.publisher = publisher;
        this.totalSales = totalSales;
        this.releaseDate = releaseDate;
    }

    public String getTitle()       { return title; }
    public String getConsole()     { return console; }
    public String getGenre()       { return genre; }
    public String getPublisher()   { return publisher; }
    public double getTotalSales()  { return totalSales; }
    public String getReleaseDate() { return releaseDate; }

    // Extract month number (1-12) from release date "YYYY-MM-DD"
    public int getMonth() {
        try {
            if (releaseDate == null || releaseDate.length() < 7) return -1;
            return Integer.parseInt(releaseDate.split("-")[1]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}