package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import java.io.IOException;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

/**
 * Homework 1 of 11791 F13: Designing Analysis Engine
 * 
 * @author Jung In Lee <junginl@cs.cmu.edu>
 */

public class QnAScoringAnnotator extends JCasAnnotator_ImplBase {
  
  /**
   * Compare each answer candidates with the question, and return the scores and the precision value.
   * 
   * @param args
   * 
   *          Having a question sentence and a set of answer candidates as input,
   * 
   *          analyzes each answer candidates (Stanford Core NLP- POS tagging, synonyms, passive voice, and negation).
   */
  
  @Override
  public void process(JCas arg0) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    // Get the document text (input).
    String docText = arg0.getDocumentText();

    // Convert the input into arrays of strings, split by lines.
    String[] lines = docText.split("/n");

    // Rearrange so that the String arrays only contain texts (without Q, A, 0, 1).
    String[] textAll = new String[lines.length];
    textAll[0] = lines[0].substring(2);
    for (int i = 1; i < lines.length; i++) {
      textAll[i] = lines[i].substring(4);
    }

    // import Stanford CoreNLP tool for POS tagging
    ArrayList<ArrayList<String>> tokenAll = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> posAll = new ArrayList<ArrayList<String>>();
    for (int i = 0; i < textAll.length; i++) {
      tokenAll.add(new ArrayList<String>());
      posAll.add(new ArrayList<String>());
      String text = textAll[i];
      Properties props = new Properties();
      props.put("annotators", "tokenize, ssplit, pos ");
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
      // create an empty Annotation just with the given text
      Annotation document = new Annotation(text);
      // run all Annotators on this text
      pipeline.annotate(document);
      List<CoreMap> sentences = document.get(SentencesAnnotation.class);
      for (CoreMap sentence : sentences) {
        // traversing the words in the current sentence
        // a CoreLabel is a CoreMap with additional token-specific methods
        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
          // this is the text of the token
          String word = token.get(TextAnnotation.class);
          // this is the POS tag of the token
          String pos = token.get(PartOfSpeechAnnotation.class);
          // Output the result
          String tokenn = token.toString();
          tokenAll.get(i).add(tokenn);
          posAll.get(i).add(pos);
        }
      }
    }

    // identifying Question's verb(VBD), subject, object
    int indVQ = 0;
    if (posAll.get(0).indexOf("VBD") != (-1)) {
      indVQ = posAll.get(0).indexOf("VBD");
    } else {
      indVQ = posAll.get(0).indexOf("VBZ");
    }
    int indSQ = posAll.get(0).indexOf("NNP");
    int indOQ = posAll.get(0).lastIndexOf("NNP");
    String VQ = tokenAll.get(0).get(indVQ);
    String SQ = tokenAll.get(0).get(indSQ);
    String OQ = tokenAll.get(0).get(indOQ);

    // identifying answers' verbs(VBDs), subjects, objects
    int[] indVA = new int[lines.length - 1];
    int[] indVPA = new int[lines.length - 1];
    String[] VA = new String[lines.length - 1];
    String[] VPA = new String[lines.length - 1];
    for (int i = 0; i < lines.length - 1; i++) {
      if (posAll.get(i + 1).indexOf("VBD") != (-1)) {
        indVA[i] = posAll.get(i + 1).indexOf("VBD");
      } else {
        indVA[i] = posAll.get(i + 1).indexOf("VBZ");
      }
      VA[i] = tokenAll.get(i + 1).get(indVA[i]);
      int x = posAll.get(i + 1).indexOf("VBN");
      if (x != -1) {
        indVPA[i] = x;
        VPA[i] = tokenAll.get(i + 1).get(indVPA[i]);
      }
    }
    int[] indSA = new int[lines.length - 1];
    String[] SA = new String[lines.length - 1];
    for (int i = 0; i < lines.length - 1; i++) {
      indSA[i] = posAll.get(i + 1).indexOf("NNP");
      SA[i] = tokenAll.get(i + 1).get(indSA[i]);
    }
    int[] indOA = new int[lines.length - 1];
    String[] OA = new String[lines.length - 1];
    for (int i = 0; i < lines.length - 1; i++) {
      indOA[i] = posAll.get(i + 1).lastIndexOf("NNP");
      OA[i] = tokenAll.get(i + 1).get(indOA[i]);
    }

    // for checking synonyms
    TreeSet<String> syn = new TreeSet<String>();
    syn.add("shot");
    syn.add("assassinated");

    // for checking passive
    TreeSet<String> auxSet = new TreeSet<String>();
    String[] aux = { "am", "is", "are", "was", "were" };
    for (String e : aux) {
      auxSet.add(e);
    }

    // for checking negation
    TreeSet<String> negSet = new TreeSet<String>();
    String[] neg = { "does", "do" };
    for (String e : neg) {
      negSet.add(e);
    }

    // comparing question with each answer
    int[] scores = new int[lines.length - 1];
    for (int i = 0; i < lines.length - 1; i++) {
      // check the verbs
      if (VQ.equals(VA[i])) {
        scores[i]++;
        // check subjects
        if (SQ.equals(SA[i])) {
          scores[i]++;
        }
        // check objects
        if (OQ.equals(OA[i])) {
          scores[i]++;
        }
      }
      // check synonyms
      else if (syn.contains(VA[i]) == true) {
        scores[i]++;
        if (SQ.equals(SA[i])) {
          scores[i]++;
        }
        if (OQ.equals(OA[i])) {
          scores[i]++;
        }
      }
      // check passive
      else if (auxSet.contains(VA[i]) == true) {
        if (VQ.equals(VPA[i])) {
          scores[i]++;
          if (SQ.equals(OA[i])) {
            scores[i]++;
          }
          if (OQ.equals(SA[i])) {
            scores[i]++;
          }
        } else if (VQ.substring(0, VQ.length() - 1)
                .equals(VPA[i].substring(0, VPA[i].length() - 1))) {
          scores[i]++;
          if (SQ.equals(OA[i])) {
            scores[i]++;
          }
          if (OQ.equals(SA[i])) {
            scores[i]++;
          }
        }
        // check passive & synonyms
        else if (syn.contains(VPA[i])) {
          scores[i]++;
          if (SQ.equals(OA[i])) {
            scores[i]++;
          }
          if (OQ.equals(SA[i])) {
            scores[i]++;
          }
        }
      }
      // check negation
      else if (negSet.contains(VA[i]) == true) {
        if (tokenAll.get(i + 1).get(indVA[i] + 1).equals("n't")) {
          if (SQ.equals(SA[i])) {
            scores[i]++;
          }
          if (OQ.equals(OA[i])) {
            scores[i]++;
          }
        }
        // to account for "does love" = "loves"
        else if (posAll.get(i + 1).get(indVA[i] + 1).equals("VB")) {
          if (tokenAll.get(i + 1).get(indVA[i] + 1).equals(VQ.substring(0, VQ.length() - 1))) {
            scores[i]++;
            if (SQ.equals(SA[i])) {
              scores[i]++;
            }
            if (OQ.equals(OA[i])) {
              scores[i]++;
            }
          }
        }
      }
    }
    System.out.println(Arrays.toString(scores));
    double[] scoresF = new double[scores.length];
    for (int i = 0; i < scores.length; i++) {
      scoresF[i] = (double) scores[i] / ((double) tokenAll.get(0).size() - 1);
    }
    System.out.println(Arrays.toString(scoresF));
    double precision = 0.0;
    double num = 0.0;
    double denom = 0.0;
    for (int i = 0; i < scoresF.length; i++) {
      if (scoresF[i] == 1.0) {
        denom = denom + 1.0;
        if (Integer.parseInt(lines[i + 1].substring(2, 3)) == 1) {
          num = num + 1.0;
        }
      }
    }
    precision = num / denom;
    System.out.println(precision);
  }
}
