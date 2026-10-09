#!/usr/bin/env python3
"""Generate Pages catalogs from repository ROM folders, gamelist.xml and theme ZIPs."""
import argparse,hashlib,json,pathlib,re,urllib.parse,zipfile,xml.etree.ElementTree as ET
SYSTEMS={'psx':'PS1','ps1':'PS1','playstation':'PS1','mame':'Arcade','mame2010':'Arcade','arcade':'Arcade','fbneo':'FBNeo','neogeo':'FBNeo','psp':'PSP','ps2':'PS2','nes':'NES','snes':'SNES','sfc':'SNES','gba':'GBA','gb':'GB','gbc':'GB','n64':'N64','genesis':'Mega Drive','megadrive':'Mega Drive','mastersystem':'Master System'}
EXTS={'.zip','.7z','.cue','.chd','.pbp','.bin','.nes','.sfc','.smc','.gba','.gb','.gbc','.z64','.n64','.v64','.md','.gen','.smd','.iso','.cso','.sms'}
def url(p):return urllib.parse.quote(str(p),safe='/')
def hashfile(p):
 h=hashlib.sha256()
 with p.open('rb') as f:
  for b in iter(lambda:f.read(1024*1024),b''):h.update(b)
 return h.hexdigest()
def generate(root,tree=None):
 if tree is None:paths={p.relative_to(root).as_posix():{'size':p.stat().st_size} for p in root.rglob('*') if p.is_file() and '.git' not in p.parts}
 else:paths={x['path']:x for x in tree['tree'] if x['type']=='blob'}
 meta={};tracks=set(); companions={}
 for rel in paths:
  p=root/rel
  if rel.endswith('/gamelist.xml') and p.exists():
   try:
    for g in ET.parse(p).getroot().findall('game'):
     base=pathlib.PurePosixPath(rel).parent; path=(base/g.findtext('path','').removeprefix('./')).as_posix();meta[path]={'title':g.findtext('name',''),'cover':(base/g.findtext('image',g.findtext('thumbnail','')).removeprefix('./')).as_posix()}
   except ET.ParseError:pass
  if rel.lower().endswith('.cue') and p.exists():
   refs=re.findall(r'^\s*FILE\s+(?:"([^"]+)"|(\S+))',p.read_text(errors='replace'),re.M|re.I)
   files=[]
   for a,b in refs:
    ref=a or b
    if '..' in pathlib.PurePosixPath(ref).parts or pathlib.PurePosixPath(ref).is_absolute():raise ValueError('Unsafe CUE track: '+ref)
    track=(pathlib.PurePosixPath(rel).parent/ref).as_posix()
    if track not in paths:raise ValueError('Missing CUE track: '+track)
    tracks.add(track);entry={'name':ref,'url':url(track),'revision':paths[track].get('sha','') or hashfile(root/track)};files.append(entry)
   companions[rel]=files
 games=[]
 for rel,info in sorted(paths.items()):
  q=pathlib.PurePosixPath(rel);system=SYSTEMS.get(q.parts[0].lower(),'')
  if not system or q.suffix.lower() not in EXTS or any(x.lower() in {'images','videos','media','bios','saves','states'} for x in q.parts[1:-1]) or rel in tracks:continue
  # Avoid routing a ZIP from the wrong platform: inspect inner extensions when local bytes exist.
  p=root/rel
  if p.exists() and q.suffix.lower()=='.zip' and system not in {'Arcade','FBNeo','PS1','PS2','PSP'}:
   try:
    inner={pathlib.PurePosixPath(x).suffix.lower() for x in zipfile.ZipFile(p).namelist()}
    allowed={'NES':{'.nes'},'SNES':{'.sfc','.smc'},'Mega Drive':{'.md','.gen','.smd','.bin'},'Master System':{'.sms','.bin'},'GBA':{'.gba'},'GB':{'.gb','.gbc'},'N64':{'.n64','.z64','.v64'}}[system]
    if not inner&allowed:print('Skipped mismatched archive:',rel);continue
   except (zipfile.BadZipFile,KeyError):continue
  m=meta.get(rel,{})
  cover=m.get('cover','')
  if cover not in paths:
   candidates=[f'{q.parent}/images/{q.stem}{s}{ext}' for s in ('-image','-screenshot','') for ext in ('.png','.jpg','.jpeg')];cover=next((x for x in candidates if x in paths),'')
  g={'id':rel,'relative':rel,'name':q.name,'system':system,'title':m.get('title') or q.stem,'url':url(rel),'cover':url(cover),'cover_revision':paths.get(cover,{}).get('sha',''),'size':info.get('size',p.stat().st_size if p.exists() else 0),'revision':info.get('sha','') or hashfile(p)}
  if rel in companions:g['files']=companions[rel]
  games.append(g)
 themes=[]
 for p in sorted((root/'themes').glob('*.zip')):
  themes.append({'id':p.stem,'title':p.stem.replace('_',' '),'url':url(p.relative_to(root)),'sha256':hashfile(p),'size':p.stat().st_size})
 for folder,name,data in [('data','games.json',games),('themes','index.json',themes)]:
  (root/folder).mkdir(exist_ok=True);(root/folder/name).write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
 print(f'{len(games)} games; {len(themes)} themes')
if __name__=='__main__':
 a=argparse.ArgumentParser();a.add_argument('--root',default='.');a.add_argument('--tree');v=a.parse_args();generate(pathlib.Path(v.root),json.load(open(v.tree)) if v.tree else None)
