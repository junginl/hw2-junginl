package edu.cmu.lti.hw2.annotators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

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
  
  /**
   * The question sentence. 
   */
  
  @Override
  public void process(JCas arg0) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    // Get the document text (input)
    String docText = arg0.getDocumentText();

    // Convert the input into arrays of strings, split by lines.
    String[] lines = docText.split("/n");

    Question annotation = new Question(arg0);
    annotation.setBegin(0);
    annotation.setEnd(lines[0].length());
    annotation.setCasProcessorId("Quesiton");
    annotation.setConfidence(1.0);
    annotation.addToIndexes();
  }
}
