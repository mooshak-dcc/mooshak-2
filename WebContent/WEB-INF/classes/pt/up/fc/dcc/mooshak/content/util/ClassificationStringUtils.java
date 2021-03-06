package pt.up.fc.dcc.mooshak.content.util;

import pt.up.fc.dcc.mooshak.content.types.Groups.Language;

public class ClassificationStringUtils {

	public static String toString(Language language, int rank) {
		switch (language) {
		case PORTUGUESE:
			return toPortugueseString(rank);

		default:
			return toEnglishString(rank);
		}
	}

	public static String toEnglishString(int rank) {
		
		if (rank == 0)
			return "Honorable Mention";
		
		return Strings.toTitle(getEnglishOrdinal(rank)) + " Place";
	}

	private static String getEnglishOrdinal(int rank) {
		
		if (rank <= 20) {
			switch (rank) {
			case 1:	return "first";
			case 2: return "second";
			case 3: return "third";
			case 4: return "fourth";
			case 5: return "fifth";
			case 6: return "sixth";
			case 7: return "seventh";
			case 8: return "eighth";
			case 9: return "ninth";
			case 10: return "tenth";
			case 11: return "eleventh";
			case 12: return "twelfth";
			case 13: return "thirteenth";
			case 14: return "fourteenth";
			case 15: return "fifteenth";
			case 16: return "sixteenth";
			case 17: return "seventeenth";
			case 18: return "eighteenth";
			case 19: return "nineteenth";
			case 20: return "twentieth";
			}
		} 
		else if (rank > 20 && rank < 30)
			return "twenty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 30)
	    	return "thirtieth";
	    else if (rank > 30 && rank < 40)
	    	return "thirty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 40)
	    	return "fortieth";
	    else if (rank > 40 && rank < 50)
	    	return "forty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 50)
	    	return "fiftieth";
	    else if (rank > 50 && rank < 60)
	    	return "fifty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 60)
	    	return "sixtieth";
	    else if (rank > 60 && rank < 70)
	    	return "sixty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 70)
	    	return "seventieth";
	    else if (rank > 70 && rank < 80)
	    	return "seventy-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 80)
	    	return "eightieth";
	    else if (rank > 80 && rank < 90)
	    	return "eighty-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 90)
	    	return "ninetieth";
	    else if (rank > 90 && rank < 100)
	    	return "ninety-" + getEnglishOrdinal(rank % 10);
	    else if (rank == 100)
	    	return "hundredth";
	    
		return "?";
	    
	}

	public static String toPortugueseString(int rank) {

		if (rank == 0)
			return "Men????o Honrosa";
		
		return Strings.toTitle(getPortugueseOrdinal(rank)) + " Lugar";
	}

	private static String getPortugueseOrdinal(int rank) {
		
		if (rank <= 10) {
			switch (rank) {
			case 1:	return "primeiro";
			case 2: return "segundo";
			case 3: return "terceiro";
			case 4: return "quarto";
			case 5: return "quinto";
			case 6: return "sexto";
			case 7: return "s??timo";
			case 8: return "oitavo";
			case 9: return "nono";
			case 10: return "d??cimo";
			}
		} 
		else if (rank > 10 && rank < 20)
			return "d??cimo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 20)
	    	return "vig??simo";
		else if (rank > 20 && rank < 30)
			return "vig??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 30)
	    	return "trig??simo";
	    else if (rank > 30 && rank < 40)
	    	return "trig??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 40)
	    	return "quadrag??simo";
	    else if (rank > 40 && rank < 50)
	    	return "quadrag??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 50)
	    	return "quinquag??simo";
	    else if (rank > 50 && rank < 60)
	    	return "quinquag??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 60)
	    	return "sexag??simo";
	    else if (rank > 60 && rank < 70)
	    	return "sexag??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 70)
	    	return "septuag??simo";
	    else if (rank > 70 && rank < 80)
	    	return "septuag??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 80)
	    	return "octog??simo";
	    else if (rank > 80 && rank < 90)
	    	return "octog??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 90)
	    	return "nonag??simo";
	    else if (rank > 90 && rank < 100)
	    	return "nonag??simo-" + getPortugueseOrdinal(rank % 10);
	    else if (rank == 100)
	    	return "cent??simo";
	    
		return "?";
	}
}
