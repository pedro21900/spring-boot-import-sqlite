package com.example.springbootimportsqlite.core.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Esta classe contém funções utilitárias para tratamento de strings.
 * 
 * @author eduaro.alcantara@tre-pa.jus.br
 * @category shared, system, util, strings
 */
public final class StringUtils {

	/**
	 * Uma lista de conectivos de nomes, como por exemplo [Silvia "da" Silva], compatíveis com a maioria dos países, e devem permanecer em caixa baixa.
	 */
	static final List<String> CONNECTIVES_SET = Arrays.asList("DE", "DI", "DO", "DA", "DOS", "DAS", "DELLO", "DELLA", "DALLA", "DAL",
			"DEL", "E", "EM", "NA", "NO", "NAS", "NOS", "VAN", "VON", "Y", "A", "E", "O", "À", "U", "É", "MAIL", "AO"
		);

	/**
	 * Uma lista de algarismos romanos, que devem permanecer em caixa alta.
	 */
	static final List<String> KEEP_UPPERCASE = Arrays.asList(
			"I", "II", "III", "IIIV", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", 
			"ZE", "JE", "TRE", "TI", /* ZE = Zona Eleitoral, JE = Justiça Eleitoral, TRE = TRE, TI = Tecnologia da Informação, PA = Pará e outros estados */			
			"PA", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PB", "PR", "PE", "PI", "RR", "RO", "RJ", "RN", "RS", "SC", "SP", "SE", "TO"
		);	
	
	static final String SPACE = " ", HYPHEN = "-", APOSTROPHE = "'", DOT = ".", DOT_COMMA = ";";
	
	static final List<String> SEPARATORS = Arrays.asList(SPACE, HYPHEN, APOSTROPHE, DOT, DOT_COMMA);
	
	/**
	 * Retorna um texto capitalizado corretamente, geralmente um nome próprio, de acordo com as regras da língua portuguesa.
	 * A função faz 4 passagens, separando palavras por espaços, traços e hífens.
	 * @return String retorna o nome ou texto normalizado, ou vazio caso o parâmetro seja nulo.
	 */
	public static String capitalizeName(String text) {		
		if (text == null) // evita error do tipo NullPointerException e um monte de if por todo o sistema
			return "";		
		text = text.trim();		
		if(text.isEmpty()) // evita processamento à toa
			return "";
		text = text.replaceAll("\n", " %%enter%% "); // mantém os separadores de frase
		text = text.replaceAll("; ", " %%dotcomma%% ");
		text = text.replaceAll("\\.\\s", " %%dot%% ");
		List<String> tempTokens = new ArrayList<String>(); // inicializa o coletor de tokens da primeira passagem 		
		List<String> spacedText = tokenize(text, SPACE); // quebra o texto por espaços, para a primeira passagem		
		for (String spacedToken : spacedText) { 
			List<String> hyphenizedText = tokenize(spacedToken, HYPHEN); // inicia a segunda passagem quebrando o texto por hífens
			tempTokens.addAll(hyphenizedText);
		}		
		List<String> tokens = new ArrayList<String>(); // inicializa o coletor de tokens da terceira passagem		
		for (String token : tempTokens) {
			List<String> spostrophizedText = tokenize(token, APOSTROPHE); // inicia a terceira passagem quebrando o texto com apóstrofos
			tokens.addAll(spostrophizedText);
		}		
		String processedText = "", comparationToken = "", wordSeparator = ""; //, phraseSeparator = "";  
		for (String token : tokens) { // na última passagem, verifica como cada palavra devem ser capitalizada			
			// phraseSeparator = token.length() > 2 ? Character.toString(token.charAt(token.length() - 2)) : "";			
			wordSeparator = token.length() > 1 ? Character.toString(token.charAt(token.length() - 1)) : "";
			/*
			if (!phraseSeparator.isEmpty() && SEPARATORS.contains(phraseSeparator)) {
				comparationToken = token.substring(0, token.length() - 2); // retira o separador no final da palavra para comparar com algarismos romanos e outros conectivos
			} else */ if (!wordSeparator.isEmpty() && SEPARATORS.contains(wordSeparator)) {
				comparationToken = token.substring(0, token.length() - 1); // retira o separador no final da palavra para comparar com algarismos romanos e outros conectivos
			} else {
				comparationToken = token;
			}
			if (CONNECTIVES_SET.contains(comparationToken.toUpperCase())) { // caso esta parte seja um conectivo
				token = token.toLowerCase();
			} else if (KEEP_UPPERCASE.contains(comparationToken.toUpperCase())) { // caso esta parte seja um algarismo romano
				token = token.toUpperCase(); // o nome já está todo em maiúscula
			} else if (comparationToken.contains("@") && comparationToken.contains(".")) { // caso esta parte seja um email
				token = token.toLowerCase();
			} else if (comparationToken.length() > 0 && comparationToken.substring(0, 1).equals("@")) { // caso esta parte seja um perfil de rede social
				// token = token // não precisa alterar nada
			} else if (comparationToken.trim().length() > 0) { // caso esta parte seja qualquer outra coisa
				token = token.substring(0, 1).toUpperCase().concat(token.substring(1).toLowerCase());				// char[] letters = token.toLowerCase().toCharArray();				// letters[0] = Character.toString(letters[0]).toUpperCase().charAt(0);				// token = String.copyValueOf(letters);				
			}			
			processedText = processedText + token; // System.out.println(String.format("token=%s, wordSeparator=%s, phraseSeparator=%s", token, wordSeparator, phraseSeparator));
		}
		processedText = capitalizePhrases(processedText, " %%enter%% ", "\n"); // restaura os separadores de frase já colocando a primeira letra de cada frase em maiúsculas
		processedText = capitalizePhrases(processedText, " %%dotcomma%% ", "; ");
		processedText = capitalizePhrases(processedText, " %%dot%% ", ". ");
		return processedText;
	}
	
	private static List<String> tokenize(String source, String splitter) {
		List<String> values = new ArrayList<String>();
		if (source == null || source.isEmpty()) 
			return values;
		if (!source.contains(splitter)) {
			values.add(source);
			return values;
		}
		values = Arrays.asList(source.split(splitter));
		for (int i = 0; i < values.size(); i++) {
			if (i < values.size() - 1)
				values.set(i, values.get(i).concat(splitter));
		}
		return values;
	}
	
	private static String capitalizePhrases(String text, String replaceFrom, String replaceTo) {
		if (!text.contains(replaceFrom))
			return text;
		String[] phrases = text.split(replaceFrom);
		text = "";
		for (String phrase : phrases) {
			String[] startsWithEmail = phrase.split(SPACE);
			if (startsWithEmail != null && startsWithEmail.length > 0 && startsWithEmail[0].contains("@") && startsWithEmail[0].contains(DOT))
				text = text.concat(phrase);
			else
				text = text.concat(replaceTo).concat(phrase.substring(0, 1).toUpperCase().concat(phrase.substring(1)));
		}
		return text;
	}

	/**
	 * Retorna a variável de text informada no parâmetro, de forma preparada para
	 * ser inclusa numa query de busca no banco de dados (não nula e minúscula).
	 */
	public static String formatSearchText(String searchText) {
		if (searchText == null)
			searchText = "";
		else
			searchText = searchText.toLowerCase();
		return searchText;
	}

	/**
	 * Retorna a string do parâmetro convertida em hash MD5.
	 */
	public static String MD5(String text) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(), 0, text.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}
	
	public static String lastCommaToAnd(String text) {
		return text.replaceFirst(",([^,]+)$", " e$1");
	}

}
