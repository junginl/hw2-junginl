package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tutorial.RoomNumber;

import java.io.IOException;

import edu.cmu.deiis.types.Answer;
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

  /**
   * Outputs the Boolean value depending on whether the answer is correct or not
   */

  @Override
  public void process(JCas arg0) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    // Get the document text (input)
    String docText = arg0.getDocumentText();

    // Convert the input into arrays of strings, split by lines.
    String[] lines = docText.split("/n");

    // loop over the answer candidates
    for (int i = 0; i < lines.length - 1; i++) {
      Answer annotation = new Answer(arg0);
      annotation.setBegin(0);
      annotation.setEnd(lines[i + 1].length());
      annotation.setCasProcessorId("Answer");
      annotation.setConfidence(1.0);
      annotation.addToIndexes();
      if (lines[i + 1].substring(2, 3).equals("1")) {
        annotation.setIsCorrect(true);
      } else if (lines[i + 1].substring(2, 3).equals("0")) {
        annotation.setIsCorrect(false);
      }
    }
  }
}
