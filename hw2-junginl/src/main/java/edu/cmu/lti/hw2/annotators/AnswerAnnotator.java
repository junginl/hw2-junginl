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
import org.apache.uima.util.Logger;

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

public class AnswerAnnotator extends JCasAnnotator_ImplBase {
  private Pattern answerPattern = Pattern.compile("\\b[0-4]\\d-[0-2]\\d\\d\\b");

  /**
   * Outputs the Boolean value depending on whether the answer is correct or not
   */
  
 
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String docText = aJCas.getDocumentText();
    
    Matcher matcher = answerPattern.matcher(docText);
  
    while (matcher.find()) {
      // found one - create annotation
      Answer answer = new Answer(aJCas);
      answer.setBegin(matcher.start());
      answer.setEnd(matcher.end());
      answer.setCasProcessorId("Question");
      answer.setConfidence(1.0);
      answer.addToIndexes();
      if ((matcher.group()).charAt(2) == '1') {
        answer.setIsCorrect(true);
      }
    }
    
    
//    // Convert the input into arrays of strings, split by lines.
//    String[] lines = docText.split("/n");
//
//    // loop over the answer candidates
//    for (int i = 0; i < lines.length - 1; i++) {
//      Answer answer = new Answer(aJCas);
//      answer.setBegin(0);
//      answer.setEnd(lines[i + 1].length());
//      answer.setCasProcessorId("Answer");
//      answer.setConfidence(1.0);
//      answer.addToIndexes();
//      if (lines[i + 1].substring(2, 3).equals("1")) {
//        answer.setIsCorrect(true);
//      } else if (lines[i + 1].substring(2, 3).equals("0")) {
//        answer.setIsCorrect(false);
//      }
//    }
  }
}
  
//    private String answersPatternString;
//    
//    public void initialize(UimaContext aContext) throws ResourceInitializationException {
//      super.initialize(aContext);
//      // Read configuration parameter values
//      setAnswersPatternString((String) getContext().getConfigParameterValue("answersPatternString"));
//  }
//    
//    @Override
//    public void process(JCas arg0) throws AnalysisEngineProcessException {
//      // TODO Auto-generated method stub
//      
//      Pattern answersPattern = Pattern.compile(this.getAnswersPatternString(), Pattern.CASE_INSENSITIVE);
//      int answerCt = 0;
//      
//      // Get the document text (input)
//      String docText = arg0.getDocumentText();
//      
//      Matcher matcher = answersPattern.matcher(docText);
//      Answer answer = null;
//      while (matcher.find()) {
//             // Create annotation of type Answers
//             answer = new Answer(arg0);
//             answer.setBegin(matcher.start());
//             answer.setEnd(matcher.end());
//             answer.setConfidence(1.0);
//             answer.setCasProcessorId("TestElementAnnotator");
//             // Check if the true result of the answer is correct. If so, change the default value (false)
//             // to true.
//             if ((matcher.group()).charAt(2) == '1') {
//               answer.setIsCorrect(true);
//             }
//             answer.addToIndexes();
//             answerCt++;
//           }
//    }
//    
//  }
//  /**
//  +   * @return the questionPatternString
//  +   */
//   public String getAnswersPatternString() {
//     return answersPatternString;
//   }
//  
//   /**
//     * @param questionPatternString the questionPatternString to set
//     */
//   public void setAnswersPatternString(String answersPatternString) {
//     this.answersPatternString = answersPatternString;
//   }
//}
