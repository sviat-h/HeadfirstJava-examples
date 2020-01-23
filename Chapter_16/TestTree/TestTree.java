package TestTree;

public class Book implements Comparable<Book> {
    String title;
    public Book(String t){
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public boolean equals(Object aBook){
        Book b = (Book) aBook;
        return getTitle().equals(b.getTitle());
    }
    public int hashCode(){
        return title.hashCode();
    }
    public int compareTo(Book b){
        return title.compareTo(b.getTitle());
    }
}

