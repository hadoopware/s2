package com.syspulse.s2.similarity

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import com.syspulse.s2.data.Video
import com.syspulse.s2.feed.Feed

import org.datavec.api.util.ClassPathResource
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import collection.JavaConverters._

object Similarity {

  val tmpFile = "/tmp/words.tmp"

  def saveFile(fileName: String, stringToWrite: String) = {
    val writer = Files.newBufferedWriter(Paths.get(fileName),Charset.defaultCharset)
    try {
      writer.write(stringToWrite)
    }finally { writer.close() }
  }

  def main(args:Array[String]):Unit = {
    val feedFile = if (args.size > 0) args(0) else "feeds/1.feed"
    val feedEngine = if (args.size > 1) args(1) else ""
    val words = if (args.size > 2) args.tail.tail.toSeq else Seq("life")

    val nnIterations = 50
    val nnLayerSize = 100
    val nnWindowSize = 5
    val nnMinWordFrequency = 5

    val feed = Feed(feedFile,feedEngine)
    Console.out.println(s"feed=${feed.getClass}")

    feed.start(feedFile)

    val txt:Seq[String] = feed.flatMap(
      _ match {
        case o:Video => Some(o.getTitle + " " + o.getDescription + " " + o.getTitleExtended)
        case a:AnyRef => Console.err.println(s"o=${a}"); None
      }
    ).toSeq

    Console.out.println(s"Objects=${txt.size}")

    val text = txt.mkString("\n")

    Console.out.println(s"Text size: ${text.size}")

    saveFile(tmpFile,text)

    Console.out.println(s"Text File:'${tmpFile}'")

    val iter: SentenceIterator  = new BasicLineIterator(tmpFile);
    val t:TokenizerFactory  = new DefaultTokenizerFactory();
    t.setTokenPreProcessor(new CommonPreprocessor());

    Console.out.println(s"Building model: (iterations=${nnIterations},layerSize=${nnLayerSize},windowSize=${nnWindowSize},minWordFequency=${nnMinWordFrequency}....");

    val vec: Word2Vec = new Word2Vec.Builder()
      .minWordFrequency(nnMinWordFrequency)
      .iterations(nnIterations)
      .layerSize(nnLayerSize)
      .seed(42)
      .windowSize(nnWindowSize)
      .iterate(iter)
      .tokenizerFactory(t)
      .build();

    Console.out.println("Fitting Word2Vec model....");

    vec.fit();

    // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
    Console.out.println(s"Closest Words: ${words}");

    for( ws <- words;
         w <- ws.split(","))
    {
      val wn = vec.wordsNearest(w, 10)
      Console.out.println(s"10 Words closest to '${w}': ${wn}");
      for(wn1 <- wn.asScala) {
        val similarity = vec.similarity(w,wn1)
        Console.out.println(s"   ${wn1} \t\t\t\t\t${similarity}");
      }
    }
  }
}
