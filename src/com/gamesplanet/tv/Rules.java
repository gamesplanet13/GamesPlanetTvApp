package com.gamesplanet.tv;
import java.io.*;import java.util.*;
public class Rules {
 public static String system(String folder,String name){
  String f="/"+folder.toLowerCase(Locale.ROOT)+"/",n=name.toLowerCase(Locale.ROOT);
  if(!n.matches(".*\\.(zip|7z|cue|chd|pbp|bin|nes|sfc|smc|gba|gb|gbc|z64|n64|v64|md|gen|smd|iso|cso)$"))return "";
  if(f.matches(".*(/psx/|/ps1/|/playstation/).*"))return "PS1";
  if(f.matches(".*(/mame/|/mame2010/|/arcade/).*"))return "Arcade";
  if(f.matches(".*(/fbneo/|/neogeo/).*"))return "FBNeo";
  if(f.matches(".*(/nes/|/famicom/).*"))return "NES";
  if(f.matches(".*(/snes/|/sfc/).*"))return "SNES";
  if(f.matches(".*(/gba/).*"))return "GBA";
  if(f.matches(".*(/gb/|/gbc/).*"))return "GB";
  if(f.matches(".*(/n64/).*"))return "N64";
  if(f.matches(".*(/megadrive/|/genesis/|/md/).*"))return "Mega Drive";
  if(f.contains("/psp/")||n.endsWith(".cso"))return "PSP";
  if(n.endsWith(".cue")||n.endsWith(".chd")||n.endsWith(".pbp")||n.endsWith(".bin"))return "PS1";
  if(n.endsWith(".nes"))return "NES";
  if(n.endsWith(".sfc")||n.endsWith(".smc"))return "SNES";
  if(n.endsWith(".gba"))return "GBA";
  if(n.endsWith(".gb")||n.endsWith(".gbc"))return "GB";
  if(n.endsWith(".z64")||n.endsWith(".n64")||n.endsWith(".v64"))return "N64";
  if(n.endsWith(".md")||n.endsWith(".gen")||n.endsWith(".smd"))return "Mega Drive";
  if(n.endsWith(".zip")||n.endsWith(".7z"))return "Arcade";
  if(n.endsWith(".iso")&&f.contains("/psp/"))return "PSP";
  return "";
 }
 public static String safeRelative(String value)throws IOException{
  String s=value.replace('\\','/');if(s.startsWith("/")||s.contains(":"))throw new IOException("Unsafe path");
  for(String p:s.split("/"))if(p.equals(".."))throw new IOException("Unsafe path");return s;
 }
 public static File child(File root,String name)throws IOException{
  File p=new File(root,safeRelative(name));String r=root.getCanonicalPath()+File.separator;
  if(!p.getCanonicalPath().startsWith(r))throw new IOException("Unsafe path");return p;
 }
 public static String core(String sys){
  switch(sys){case "PS1":return "pcsx_rearmed";case "Arcade":return "mame2010";case "FBNeo":return "fbneo";case "NES":return "fceumm";case "SNES":return "snes9x";case "GB":return "gambatte";case "GBA":return "mgba";case "Mega Drive":return "genesis_plus_gx";case "N64":return "mupen64plus_next";case "PSP":return "ppsspp";default:return "";}
 }
}
