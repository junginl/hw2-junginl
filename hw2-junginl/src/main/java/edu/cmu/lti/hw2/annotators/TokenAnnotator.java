package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.IOException;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Token;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
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

public class TokenAnnotator extends JCasAnnotator_ImplBase {
  
  /**
   * Tokens
   */
//  
//  
//  @Override
//  public void process(JCas aJCas) throws AnalysisEngineProcessException {
//    
//    //offset to obtain text only
//    int posInd = 0;
//    int qOffset = 2;
//    int aOffset = 4;
//    int endLineChar = 2;
//    
//    //Retrieve the question
//    Question question = JCasUtil.selectSingle(aJCas, Question.class);
//    
//    //Get the document text (input).
//    String docText = aJCas.getDocumentText();
//    String[] lines = docText.split("/n");
//    String question = lines[0];
//    
//    // TODO Auto-generated method stub
//    // Get the document text (input)
//
//    // Convert the input into arrays of strings, split by lines.
//    String[] lines = docText.split("/n");
//
//    Token annotation = new Token(arg0);
//    annotation.setBegin(0);
//    annotation.setEnd(lines[0].length());
//    annotation.setCasProcessorId("Token");
//    annotation.setConfidence(1.0);
//    annotation.addToIndexes();
//         
//    //Rearrange so that the String arrays only contain texts (without Q, A, 0, 1).
//    String[] textAll = new String[lines.length];
//    textAll[0] = lines[0].substring(2);
//    for (int i=1; i<lines.length; i++) {
//      textAll[i] = lines[i].substring(4);
//    }
////    
////
////    //Use Stanford CoreNLP tool for POS tagging
////    ArrayList<ArrayList<String>> tokenAll = new ArrayList<ArrayList<String>>();
////    ArrayList<ArrayList<String>> posAll = new ArrayList<ArrayList<String>>();
////    for (int i = 0; i < textAll.length; i++) {
////      tokenAll.add(new ArrayList<String>());
////      posAll.add(new ArrayList<String>());
////      String text = textAll[i];
////      Properties props = new Properties();
////      props.put("annotators", "tokenize, ssplit, pos ");
////      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
////      // create an empty Annotation just with the given text
////      Annotation document = new Annotation(text);
////      // run all Annotators on this text
////      pipeline.annotate(document);
////      List<CoreMap> sentences = document.get(SentencesAnnotation.class);
////      for (CoreMap sentence : sentences) {
////        // traversing the words in the current sentence
////        // a CoreLabel is a CoreMap with additional token-specific methods
////        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
////          // this is the text of the token
////          String word = token.get(TextAnnotation.class);
////          // this is the POS tag of the token
////          String pos = token.get(PartOfSpeechAnnotation.class);
////          // Output the result
////          String tokenn = token.toString();
////          tokenAll.get(i).add(tokenn);
////          posAll.get(i).add(pos);
////        }
////      }
////    }
//  }
//  
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    
    //offset to obtain text only
    int posInd = 0;
    int qOffset = 2;
    int aOffset = 4;
    int endLineChar = 2;

    //Retrieve the question
    Question question = JCasUtil.selectSingle(aJCas, Question.class);

    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    //Tokenize the question sentence
    String questionSent = question.getCoveredText();
    String questionText = questionSent.substring(qOffset);

    // create an empty Annotation just with the given text
    Annotation document = new Annotation(questionText);

    //run all Annotators on this text
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    String word = null;
    String pos = null;
    int startInd = 0;
    int endInd = 0;
    for (CoreMap sentence : sentences) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // this is the text of the token
        word = token.get(TextAnnotation.class);
        // this is the POS tag of the token
        pos = token.get(PartOfSpeechAnnotation.class);
    
        //create Token annotation object
        Token wordToken = new Token(aJCas);
    
        startInd = posInd + token.get(CharacterOffsetBeginAnnotation.class) + qOffset;
        endInd = posInd + token.get(CharacterOffsetEndAnnotation.class) + qOffset;
        wordToken.setBegin(startInd);
        wordToken.setEnd(endInd);
        wordToken.setCasProcessorId("TokenAnnotator");
        wordToken.setConfidence(1.0);
        wordToken.addToIndexes();
      }
      posInd += questionSent.length() + endLineChar;
  
      //retrieve answers
      List<Answer> answerList = new ArrayList<Answer>(JCasUtil.select(aJCas, Answer.class));
      Iterator<Answer> iter = answerList.iterator();
      Answer answer = null;
      String answerSent = null;
      String answerText = null;
  
      //tokenize the answers
      while(iter.hasNext()) {
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        pipeline = new StanfordCoreNLP(props);
        answer = iter.next();
    
        answerSent = answer.getCoveredText();
        answerText = answerSent.substring(aOffset);
        document = new Annotation(answerText);
    
        //run all Annotators on this text
        pipeline.annotate(document);
        sentences = document.get(SentencesAnnotation.class);
        for (CoreMap s1 : sentences) {
          // traversing the words in the current sentence
          // a CoreLabel is a CoreMap with additional token-specific methods
          for (CoreLabel token : s1.get(TokensAnnotation.class)) {
            // this is the text of the token
            word = token.get(TextAnnotation.class);
            // this is the POS tag of the token
            pos = token.get(PartOfSpeechAnnotation.class);
        
            //create Token annotation object
            Token wordToken = new Token(aJCas);
        
            startInd = posInd + token.get(CharacterOffsetBeginAnnotation.class) + aOffset;
            endInd = posInd + token.get(CharacterOffsetEndAnnotation.class) + aOffset;
            wordToken.setBegin(startInd);
            wordToken.setEnd(endInd);
            wordToken.setCasProcessorId("TokenAnnotator");
            wordToken.setConfidence(1.0);
            wordToken.addToIndexes();
       
          }
        }
        posInd += answerSent.length() + endLineChar;
      }
    }
  }
}
