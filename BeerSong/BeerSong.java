public class BeerSong {
	public static void main (String[] args) {

		int beerNum = 99;
		String word = "бутылок";

		while (beerNum > 0) {
		
		if (beerNum == 1) {
		word = "бутылка";
}
		if (beerNum < 0) {	
System.out.println(beerNum + " " + word + " пива на стене");
System.out.println(beerNum + " " + word + " пива");
System.out.println("возьми одну");
System.out.println("пусти по кругу");
	beerNum = beerNum - 1;
	
		System.out.println(beerNum + " " + word + "  пива на стене");
} else {
		System.out.println("Нет бутылок пива на стене");
}
break;
}
}
}
