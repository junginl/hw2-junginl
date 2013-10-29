package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import java.io.IOException;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Question;
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
 * Homework 2 of 11791 F13: Designing Analysis Engine
 * 
 * @author Jung In Lee <junginl@cs.cmu.edu>
 */

public class QuestionAnnotator extends JCasAnnotator_ImplBase {
  
  private Pattern questionPattern = Pattern.compile("\\b[0-4]\\d-[0-2]\\d\\d\\b");
  /**
   * The question sentence. 
   */
  
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
  
    //Get the document text (input)
    String docText = aJCas.getDocumentText();
    
    Matcher matcher = questionPattern.matcher(docText);
    int pos = 0;
    while (matcher.find(pos)) {
      // found one - create annotation
      Question question = new Question(aJCas);
      question.setBegin(matcher.start());
      question.setEnd(matcher.end());
      question.setCasProcessorId("Question");
      question.setConfidence(1.0);
      question.addToIndexes();
      pos = matcher.end();
    }
//    // Convert the input into arrays of strings, split by lines.
//    String[] lines = docText.split("/n");
//
//    Question question = new Question(aJCas);
//    question.setBegin(0);
//    question.setEnd(lines[0].length());
//    question.setCasProcessorId("Quesiton");
//    question.setConfidence(1.0);
//    question.addToIndexes();
  }
  
//private String questionPatternString;
//
//public void initialize(UimaContext aContext) throws ResourceInitializationException {
//     super.initialize(aContext);
//     // Read configuration parameter values
//     setQuestionPatternString((String) getContext().getConfigParameterValue("questionPatternString"));
//}
//
//@Override
//public void process(JCas arg0) throws AnalysisEngineProcessException {
//  // TODO Auto-generated method stub
//  // Get the document text (input)
//  String docText = arg0.getDocumentText();
//
//  Pattern questionPattern = Pattern.compile(this.getQuestionPatternString(), Pattern.CASE_INSENSITIVE);
//  
//  Matcher matcher = questionPattern.matcher(docText);
//  if (matcher.find()) {
//        // Create annotation of type Question
//         Question question = new Question(arg0);
//         question.setBegin(matcher.start());
//         question.setEnd(matcher.end());
//         question.setConfidence(1.0);
//         question.setCasProcessorId("Question");
//         question.addToIndexes();
//       }
  
//  /**
//  +   * @return the questionPatternString
//  +   */
//   public String getQuestionPatternString() {
//     return questionPatternString;
//   }
//  
//   /**
//     * @param questionPatternString the questionPatternString to set
//     */
//   public void setQuestionPatternString(String questionPatternString) {
//     this.questionPatternString = questionPatternString;
//   }
}
