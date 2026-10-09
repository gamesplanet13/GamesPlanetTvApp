import com.gamesplanet.tv.Rules;import java.io.*;
public class RulesTest {
 static void same(String got,String expected){if(!got.equals(expected))throw new AssertionError(got+" != "+expected);}
 public static void main(String[]args)throws Exception{
  same(Rules.system("psx","TEKKEN 3.zip"),"PS1");same(Rules.system("nes","Mario.zip"),"NES");same(Rules.system("snes","World.zip"),"SNES");same(Rules.system("megadrive","Sonic.zip"),"Mega Drive");same(Rules.system("mame","tektagt.zip"),"Arcade");same(Rules.system("fbneo","mslug.zip"),"FBNeo");same(Rules.system("psp","Game.iso"),"PSP");same(Rules.system("","Game.cue"),"PS1");same(Rules.system("","gamelist.xml"),"");same(Rules.system("psx","gamelist.xml"),"");same(Rules.system("nes","readme.txt"),"");same(Rules.system("mame","cover.png"),"");same(Rules.system("gba","Game.gba"),"GBA");same(Rules.core("PS1"),"pcsx_rearmed");same(Rules.core("Arcade"),"mame2010");
  for(String bad:new String[]{"../evil","/tmp/evil","assets/../../evil","C:/evil","..\\evil"}){try{Rules.child(new File("/tmp/themes-test"),bad);throw new AssertionError("Traversal accepted: "+bad);}catch(IOException expected){}}
  same(Rules.child(new File("/tmp/themes-test"),"assets/bg.jpg").getPath(),"/tmp/themes-test/assets/bg.jpg");
  System.out.println("PASS: ROM routing, core mapping, unsupported-file filtering, safe archive paths");
 }
}
