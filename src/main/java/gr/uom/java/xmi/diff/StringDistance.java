package gr.uom.java.xmi.diff;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class StringDistance {
	
	public static int editDistance(String a, String b, int threshold) {
		return StringUtils.getLevenshteinDistance(a, b, threshold);
	}

	public static int editDistance(String a, String b) {
		return StringUtils.getLevenshteinDistance(a, b);
	}
	
	public static List<String> commonSubstrings(String s1, String s2) {
		List<String> commonSubstrings = new ArrayList<String>();
		int m = s1.length();
		int n = s2.length();
		int[][] num = new int[m][n];
		int maxlen = 0;
		int lastSubsBegin = 0;
		StringBuilder sequence = new StringBuilder();
		for(int i=0; i<m; i++) {
			for(int j=0; j<n; j++) {
				if(s1.charAt(i) != s2.charAt(j))
					num[i][j] = 0;
				else {
					if ((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i-1][j-1];
					if(num[i][j] > maxlen) {
						maxlen = num[i][j];
						int thisSubsBegin = i - num[i][j] + 1;
						if (lastSubsBegin == thisSubsBegin)
							sequence.append(s1.charAt(i));
						else {
							lastSubsBegin = thisSubsBegin;
							commonSubstrings.add(sequence.toString());
							sequence = new StringBuilder();
							sequence.append(s1.substring(lastSubsBegin, i+1));
						}
					}
				}
			}
		}
		commonSubstrings.add(sequence.toString());
		return commonSubstrings;
	}
}
