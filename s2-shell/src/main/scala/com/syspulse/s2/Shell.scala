package com.syspulse.s2

import com.syspulse.s2.data.Video
import com.syspulse.s2.feed.Feed
import com.syspulse.s2.grid.mem.MemGrid
import com.syspulse.s2.index.{IndexerGen, IndexerVideo}
import com.typesafe.scalalogging.Logger
import java.io.{StringWriter, PrintWriter}

import org.jline.terminal._
import org.jline.reader._

import collection.JavaConverters._

trait Command {
	val logger = Logger(this.getClass)
    def exec(ctx:Context,args:Seq[String])(implicit out:PrintWriter):Either[String,Context]
}

class Context(val name:String, val data:Seq[Video] = Seq(),val indexer:Option[IndexerGen[_]] = None){

    def withData(data:Seq[Video]):Context = {
        new Context(this.name,data,this.indexer)
    }

    def withIndexer(indexer:Option[IndexerGen[_]]):Context = {
        new Context(this.name,this.data,indexer)
    }

    override def toString = s"(${data.size}):${indexer}: >"
}

object LoadCommand extends Command {
    def exec(ctx:Context, args: Seq[String])(implicit out:PrintWriter): Either[String, Context] = {
        val feedFile = if(args.size>0) args(0) else return Left("file missing")
        val feedEngine = if(args.size>1) args(1) else ""

        try {
            val feed = Feed(feedFile,feedEngine)
            out.println("feeder='%s'\n".format(feed.getClass))

            feed.start(feedFile)

            val data: Seq[Video] = feed.flatMap(
                _ match {
                    case o: Video => Some(o)
                    case a: AnyRef => out.println("o=%s\n".format(a)); None
                }
            ).toList

            logger.debug("data=%s\n", data)

            for (o <- data) {
                logger.debug("o=[%s]\n", o.toString)
            }

            Right(ctx.withData(data))
        } catch {
            case e:Exception => { val sw =new StringWriter(); val pr = new PrintWriter(sw); e.printStackTrace(pr); Left(sw.toString) }
        }
    }
}


object IndexCommand extends Command {
    def exec(ctx:Context,args: Seq[String])(implicit out:PrintWriter): Either[String, Context] = {

        try {
            val indexer = new IndexerVideo with MemGrid {}

            val idx = indexer.fromObjects("title", Nil, Nil, 10, ctx.data)
            //val idx = indexer.fromObjects("title",Seq("category"),Seq("TV"),10,data)
            //indexer.fromObjects("title",Seq("category","ratingMPAA"),Seq("TV","TVPG"),10,data)
            //indexer.fromObjects("rating",Seq(),Seq(),10,data)
            //indexer.fromObjects("rating",Seq("adult"),Seq(true),10,data)
            //val idx = indexer.fromObjects("title",Seq("subcategories","subcategories"),Seq("Kids","Animation"),10,data)


            indexer.insert(idx)
            Right(ctx.withIndexer(Some(indexer)))
        } catch {
            case e:Exception => { val sw =new StringWriter(); val pr = new PrintWriter(sw); e.printStackTrace(pr); Left(sw.toString) }
        }
    }
}

object HelpCommand extends Command {
    def exec(ctx:Context,args: Seq[String])(implicit out:PrintWriter): Either[String, Context] = {
        out.println(
          """
            |load <file> [feeder]   - load data
            |index                  - index data
            |exit                   - exit
          """.stripMargin)
        Right(ctx)
    }
}


object Shell {

    val commands = Map[String,Command](
        "load" -> LoadCommand,
        "index" -> IndexCommand,
        "help" -> HelpCommand
    )

    var ctx:Option[Context] = None

    def prompt:String = {
        ctx.orElse(Some("> ")).get.toString
    }

    def main(args: Array[String]):Unit = {
        run
    }

    def parse(line:String):Seq[String] = line.split("\\S+").drop(1)

    def run = {
        val mask = null
        val color = false

		val terminal = TerminalBuilder.builder()
      .system(true)
      .signalHandler(Terminal.SignalHandler.SIG_IGN)
      .build();

		val reader = LineReaderBuilder.builder()
                              .terminal(terminal)
                              //.completer(new MyCompleter())
                              //.highlighter(new MyHighlighter())
                              //.parser(new MyParser())
                              .build();
        //reader.setPrompt("\u001B[1mfoo\u001B[0m@bar\u001B[32m@baz\u001B[0m> ");
        //val completors = new LinkedList<Completer>();

        var line:String = ""
        var echo:Boolean = true
        implicit val out = terminal.writer()//new PrintWriter(reader.getOutput())

        var exit=false
        try {
            while (!exit && { line = reader.readLine(prompt); line} != null)
            {

                if (echo) out.println(line)
                terminal.flush()

                // If we input the special word then we will mask
                // the next line.
                //            if ((trigger != null) && (line.compareTo(trigger) == 0)) {
                //                line = reader.readLine("password> ", mask);
                //            }

                val pl: ParsedLine = reader.getParser().parse(line, 0);

                pl.word() match {
                    case "exit" => exit = true
                    case s: String => {
                        commands.get(s).map(cmd => {
                            val r = cmd.exec(ctx.getOrElse(new Context("none")),pl.words().asScala.toSeq.tail)
                            ctx =
                              r match {
                                case Right(ctxNew) => Some(ctxNew)
                                case Left(error) => ctx
                            }
                            out.println(r)
                        })
                    }
                }

            }
        }catch {
            case e:org.jline.reader.UserInterruptException => {
                System.exit(1);
            }
        }
    }
}
