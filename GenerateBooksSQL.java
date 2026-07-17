import java.io.FileWriter;
import java.io.IOException;

public class GenerateBooksSQL {

    static String[] productivity = {
            "Atomic Habits","Deep Work","The 7 Habits","Eat That Frog",
            "The 5 AM Club","Getting Things Done","Think and Grow Rich",
            "The One Thing","Mindset","The Power of Habit",
            "Rich Dad Poor Dad","The Magic of Thinking Big",
            "The Compound Effect","Essentialism","The Miracle Morning",
            "The Success Principles","Start With Why","Grit",
            "Outliers","Drive","Ikigai","Unlimited Power",
            "Can't Hurt Me","Peak","Make Time"
    };

    static String[] science = {
            "Dune","Foundation","The Martian","The Time Machine",
            "Ender's Game","Ready Player One","Neuromancer",
            "I Robot","Fahrenheit 451","Brave New World",
            "Snow Crash","Hyperion","Childhood's End",
            "The Forever War","Solaris","The Expanse",
            "Red Rising","Project Hail Mary","Ringworld",
            "The Left Hand of Darkness","Do Androids Dream",
            "2001 A Space Odyssey","The Moon is a Harsh Mistress",
            "The Three Body Problem","The Invisible Man"
    };

    static String[] thriller = {
            "Sherlock Holmes","Gone Girl","The Da Vinci Code",
            "The Silent Patient","Murder on the Orient Express",
            "The Firm","The Client","Killing Floor",
            "The Girl with the Dragon Tattoo","Angels and Demons",
            "Inferno","Origin","Digital Fortress",
            "Presumed Innocent","The Appeal","The Broker",
            "The Partner","The Pelican Brief","A Time To Kill",
            "The Runaway Jury","The Last Juror","The Testament",
            "The Reversal","The Racketeer","The Whistler"
    };

    static String[] novels = {
            "The Alchemist","1984","Pride and Prejudice",
            "The Hobbit","The Great Gatsby","To Kill a Mockingbird",
            "The Kite Runner","The Book Thief","Little Women",
            "The Fault in Our Stars","The Catcher in the Rye",
            "The Hunger Games","Life of Pi","Animal Farm",
            "Jane Eyre","Dracula","Frankenstein",
            "The Giver","The Outsiders","The Road",
            "The Shack","A Walk to Remember","The Help",
            "The Old Man and the Sea","The Little Prince"
    };

    static String[] authors = {
            "James Clear","Cal Newport","Stephen Covey","Brian Tracy",
            "Robin Sharma","David Allen","Napoleon Hill",
            "Gary Keller","Carol Dweck","Charles Duhigg",
            "Robert Kiyosaki","Isaac Asimov","Frank Herbert",
            "H G Wells","Andy Weir","Dan Brown",
            "Agatha Christie","Arthur Conan Doyle","John Grisham",
            "George Orwell","Paulo Coelho","Jane Austen",
            "J R R Tolkien","Harper Lee","Khaled Hosseini"
    };

    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("books.sql");

        writer.write("USE InnerVoiceProject;\n\n");

        int img = 1;

        generate(writer, productivity, "Productivity", img);
        img += 25;

        generate(writer, science, "Science Fiction", img);
        img += 25;

        generate(writer, thriller, "Crime Thrillers", img);
        img += 25;

        generate(writer, novels, "Novels", img);

        writer.close();

        System.out.println("books.sql generated successfully.");
    }

    static void generate(FileWriter writer, String[] books,
                         String type, int start) throws IOException {

        for(int i=0;i<books.length;i++){

            String author=authors[i%authors.length];

            String category=(i%3==0)?"Premium":"Free";

            writer.write(
                    "INSERT INTO book(book_author,book_name,book_category,book_type,image_link,audio_file_link) VALUES("+
                            "'" + author + "',"+
                            "'" + books[i] + "',"+
                            "'" + category + "',"+
                            "'" + type + "',"+
                            "'book"+(start+i)+".jpg',"+
                            "'audio"+(start+i)+".mp3');\n"
            );
        }

        writer.write("\n");
    }

}