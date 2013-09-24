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

public class NGramAnnotator extends JCasAnnotator_ImplBase {
  
  /**
   * N Gram
   */
  
  @Override
  public void process(JCas arg0) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    //Get the document text (input).
    String docText = arg0.getDocumentText();

    //Convert the input into arrays of strings, split by lines.
    String[] lines = docText.split("/n");
    
    //Rearrange so that the String arrays only contain texts (without Q, A, 0, 1).
    String[] textAll = new String[lines.length];
    textAll[0] = lines[0].substring(2);
    for (int i=1; i<lines.length; i++) {
      textAll[i] = lines[i].substring(4);
    }
    

    /**
     * Use Stanford CoreNLP tool for POS tagging
     */ 
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
  }
}
