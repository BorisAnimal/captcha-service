package captchaServer.infrastructure.dataset

import java.awt.image.BufferedImage
import java.io.File

import captchaServer.domain.captcha.{Dataset, SymbolPicker}
import javax.imageio.ImageIO

object DatasetLoader {
  protected def getListOfFiles(dir: String): List[String] = {
    // TODO: catch FileNotFound
    val files = new File(dir).listFiles
    files.map(_.getAbsolutePath).toList
  }

  def loadDataset(fromDirectory: String): Dataset[BufferedImage] = {
    val classDirs = getListOfFiles(fromDirectory)
    val classSymbols = classDirs.map(new File(_).getName)

    val tuples: Seq[(String, SymbolPicker[BufferedImage])] = for {
      (dir, symbol) <- classDirs zip classSymbols
      images = getListOfFiles(dir)
    } yield symbol -> SymbolPicker(images.map(p => ImageIO.read(new File(p))))

    Dataset(tuples.toMap[String, SymbolPicker[BufferedImage]])
  }
}
