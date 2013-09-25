package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tutorial.RoomNumber;
import org.apache.uima.cas.FSIndex;

import java.io.IOException;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
//import edu.cmu.deiis.types.Answer;
//import edu.cmu.deiis.types.AnswerScore;
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

public class AnswerScoreAnnotator extends JCasAnnotator_ImplBase {
  
  /**
   * Compare each answer candidates with the question, and return the scores and the precision value.
   * 
   */
  
  @Override
  public void process(JCas arg0) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    //Get the document text (input).
    String docText = arg0.getDocumentText();
    
//    AnswerScore annotation = new AnswerScore(arg0);
//    annotation.setBegin(0);
//    annotation.setEnd(1);
//    annotation.setCasProcessorId("AnswerScore");

    // get annotation indexes
    FSIndex answerIndex = arg0.getAnnotationIndex(Answer.type);
    
    //Convert the input into arrays of strings, split by lines.
    String[] lines = docText.split("/n");
    
    //Rearrange so that the String arrays only contain texts (without Q, A, 0, 1).
    String[] textAll = new String[lines.length];
    textAll[0] = lines[0].substring(2);
    for (int i=1; i<lines.length; i++) {
      textAll[i] = lines[i].substring(4);
    }
    
 
     //Use Stanford CoreNLP tool for POS tagging 
    ArrayList<ArrayList<String>> tokenAll = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> posAll = new ArrayList<ArrayList<String>>();
    Iterator answerIter = answerIndex.iterator();
    
    ArrayList<Double> scoresLs = new ArrayList<Double>();
    
    while (answerIter.hasNext()) {
      Answer answer = (Answer) answerIter.next();
      String answerSen = answer.getCoveredText();

      String[] textPos = new String[2];
      textPos[0] = textAll[0];
      textPos[1] = answerSen;
      for (int i = 0; i < textPos.length; i++) {
        tokenAll.add(new ArrayList<String>());
        posAll.add(new ArrayList<String>());
        String text = textPos[i];
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

      // Analyze each answer candidates with regards to the Question.
      // identifying Question's verb(VBD/VBZ, VBN), subject, object
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
      int indVA;
      int indVPA;
      String VA;
      String VPA = "";

      if (posAll.get(1).indexOf("VBD") != (-1)) {
        indVA = posAll.get(1).indexOf("VBD");
      } else {
        indVA = posAll.get(1).indexOf("VBZ");
      }
      VA = tokenAll.get(1).get(indVA);
      int x = posAll.get(1).indexOf("VBN");
      if (x != -1) {
        indVPA = x;
        VPA = tokenAll.get(1).get(indVPA);
      }

      int indSA;
      String SA;
      indSA = posAll.get(1).indexOf("NNP");
      SA = tokenAll.get(1).get(indSA);

      int indOA;
      String OA;
      indOA = posAll.get(1).lastIndexOf("NNP");
      OA = tokenAll.get(1).get(indOA);

      // for checking synonyms
      TreeSet<String> syn = new TreeSet<String>();
      syn.add("shot");
      syn.add("assassinated");

      // for checking passive voice
      TreeSet<String> auxSet = new TreeSet<String>();
      String[] aux = { "am", "is", "are", "was", "were" };
      for (String e : aux) {
        auxSet.add(e);
      }

      // for checking negation
      TreeSet<String> negSet = new TreeSet<String>();
      String[] neg = { "does", "do", "did" };
      for (String e : neg) {
        negSet.add(e);
      }

      // comparing question with each answer
      double scores = 0.0;
      // check the verbs
      if (VQ.equals(VA)) {
        scores = scores + 1.0;
        // check subjects
        if (SQ.equals(SA)) {
          scores = scores + 1.0;
        }
        // check objects
        if (OQ.equals(OA)) {
          scores = scores + 1.0;
        }
      }

      // check synonyms
      else if (syn.contains(VA) == true) {
        scores = scores + 1.0;
        if (SQ.equals(SA)) {
          scores = scores + 1.0;
        }
        if (OQ.equals(OA)) {
          scores = scores + 1.0;
        }
      }
      // check passive
      else if (auxSet.contains(VA) == true) {
        if (VQ.equals(VPA)) {
          scores = scores + 1.0;
          if (SQ.equals(OA)) {
            scores = scores + 1.0;
          }
          if (OQ.equals(SA)) {
            scores = scores + 1.0;
          }
        } else if (VQ.substring(0, VQ.length() - 1).equals(VPA.substring(0, VPA.length() - 1))) {
          scores = scores + 1.0;
          if (SQ.equals(OA)) {
            scores = scores + 1.0;
          }
          if (OQ.equals(SA)) {
            scores = scores + 1.0;
          }
        }
        // check passive & synonyms
        else if (syn.contains(VPA)) {
          scores = scores + 1.0;
          if (SQ.equals(OA)) {
            scores = scores + 1.0;
          }
          if (OQ.equals(SA)) {
            scores = scores + 1.0;
          }
        }
      }
      // check negation
      else if (negSet.contains(VA) == true) {
        if (tokenAll.get(1).get(indVA + 1).equals("n't")) {
          if (SQ.equals(SA)) {
            scores = scores + 1.0;
          }
          if (OQ.equals(OA)) {
            scores = scores + 1.0;
          }
        }
        // to account for "does love" = "loves"
        else if (posAll.get(1).get(indVA + 1).equals("VB")) {
          if (tokenAll.get(1).get(indVA + 1).equals(VQ.substring(0, VQ.length() - 1))) {
            scores = scores + 1.0;
            if (SQ.equals(SA)) {
              scores = scores + 1.0;
            }
            if (OQ.equals(OA)) {
              scores = scores + 1.0;
            }
          }
        }
      }

      // Computing scores:
      // Each sentence gets one point for getting each of the Subject, Verb, Object correct
      // and then the obtained score is divided to the total number of tokens of the question
      // sentence (excluding punctuations).
      double scoresF;
      scoresF = (double) scores / (tokenAll.get(0).size() - 1);
      AnswerScore annotation = new AnswerScore(arg0);
      annotation.setScore(scoresF);
      scoresLs.add(scoresF);
    }
  //Computing precision: # of correct sentences / # sentences the system purports to be correct
    double precision = 0.0;
    int num = 0;
    int denom = 0;
    for (int i=0; i<scoresLs.size(); i++) {
      if (scoresLs.get(i)==1.0) {
        denom ++;
        if (Integer.parseInt(lines[i+1].substring(2,3))==1) {
          num ++;
        }
      }
    }
    precision = num / denom;
  }
}
