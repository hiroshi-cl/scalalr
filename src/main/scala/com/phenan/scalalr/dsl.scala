package com.phenan.scalalr

import shared._
import macroimpl._

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros

class dsl [T] extends StaticAnnotation {
  def macroTransform (annottees: Any*): Any = macro dslBundle.impl
}

class dslBundle (val c: scala.reflect.macros.whitebox.Context)
  extends TreeGeneratorModule with CodeGeneratorModule with LALRAutomatonModule
    with SyntaxGeneratorModule with TyperModule with SyntaxInfoCollectorModule with SyntaxInfoModule
    with SyntaxRuleModule with AnnotationFinderModule with CommonNamesModule with MacroUtilitiesModule with MacroModule
{
  import c.universe._

  /**
    * マクロのエントリーポイント
    * @param annottees マクロ変換対象となる構文木
    * @return マクロ変換後の構文木
    */
  def impl (annottees: Tree*): Tree = {
    q"""
      ${translate(annottees.head)}
      ..${annottees.tail}
      """
  }

  /**
    * object のボディを変換する
    * @param tree マクロ変換対象となる構文木
    * @return マクロ変換後の構文木
    */
  private def translate (tree: Tree): Tree = {
    val (t, s) = processSyntaxAnnotations(tree)
    doTypeCheck(t)
    val syntax = generateSyntax(s)
    val generated = CodeGenerator(LALRAutomaton(syntax)).generatedDefinitions
    MacroUtilities.addMembers(t, generated)
  }
}
