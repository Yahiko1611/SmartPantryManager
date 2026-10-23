from pathlib import Path
import re, xml.etree.ElementTree as ET, subprocess, tempfile, textwrap
root=Path(__file__).resolve().parents[1]
generated_dirs={'.gradle','build'}
xmls=[f for f in root.rglob('*.xml') if not generated_dirs.intersection(f.relative_to(root).parts)]
for f in xmls:
    ET.parse(f)
print(f'XML parse: {len(xmls)} files passed')
# Reference check for layout/drawable/color/style/string resources.
known={k:set() for k in ['layout','drawable','color','style','string','mipmap','id']}
for f in root.joinpath('app/src/main/res').rglob('*'):
    if f.is_file():
        if f.parent.name.startswith('values'):
            try:
                rt=ET.parse(f).getroot()
                for el in rt:
                    if el.tag in known and el.attrib.get('name'): known[el.tag].add(el.attrib['name'])
            except Exception: pass
        elif f.parent.name in known:
            known[f.parent.name].add(f.stem)
        if f.suffix == '.xml':
            for rid in re.findall(r'@\+id/([A-Za-z0-9_]+)', f.read_text(errors='ignore')):
                known['id'].add(rid)
errors=[]
for f in xmls:
    tx=f.read_text(errors='ignore')
    for namespace, kind, name in re.findall(r'@(?:(android):)?(layout|drawable|color|string|style|mipmap)/([A-Za-z0-9_]+)', tx):
        if namespace == 'android': continue
        if kind in known and name not in known[kind] and not kind=='style' and not name.startswith('android'):
            errors.append(f'{f.relative_to(root)} references missing {kind}/{name}')
java_files=list(root.joinpath('app/src/main/java').rglob('*.java'))
java='\n'.join(f.read_text(errors='ignore') for f in java_files)
for kind,name in re.findall(r'(?<!android\.)R\.(layout|drawable|color|string|style|id)\.([A-Za-z0-9_]+)',java):
    if name not in known.get(kind,set()): errors.append(f'Java references missing R.{kind}.{name}')
if errors:
    print('\n'.join(errors)); raise SystemExit(1)
print(f'Resource references: {len(java_files)} Java files passed')
seed_count=len(re.findall(r'new SeedRecipe\(', root.joinpath('app/src/main/java/za/co/smartpantry/data/DatabaseHelper.java').read_text()))
assert seed_count==20, seed_count
print(f'Recipe seed count: {seed_count}')
assert len(list(root.joinpath('app/src').rglob('*.kt')))==0
print('Java-only source: passed')
# Compile and execute the standalone ingredient normalizer class.
normalizer=root/'app/src/main/java/za/co/smartpantry/util/IngredientNormalizer.java'
with tempfile.TemporaryDirectory() as td:
    td=Path(td)
    harness=td/'NormalizerSmokeTest.java'
    harness.write_text(textwrap.dedent('''
        import za.co.smartpantry.util.IngredientNormalizer;
        public class NormalizerSmokeTest {
          public static void main(String[] args) {
            check("tomato", IngredientNormalizer.normalizeName("tomatoes"));
            check("berry", IngredientNormalizer.normalizeName("berries"));
            check("sweet potato", IngredientNormalizer.normalizeName("sweet potatoes"));
            check("banana", IngredientNormalizer.normalizeName("banana"));
            check("oat", IngredientNormalizer.normalizeName("oats"));
            double kg = IngredientNormalizer.toMeasure(2, "kg").baseQuantity;
            double tbsp = IngredientNormalizer.toMeasure(2, "tbsp").baseQuantity;
            if (Math.abs(kg - 2000) > .001) throw new RuntimeException("kg conversion failed");
            if (Math.abs(tbsp - 30) > .001) throw new RuntimeException("tbsp conversion failed");
            System.out.println("Normalizer smoke tests passed (5 name checks, 2 unit conversions)");
          }
          static void check(String expected, String actual) {
            if (!expected.equals(actual)) throw new RuntimeException(expected + " != " + actual);
          }
        }
    '''))
    out=td/'classes'; out.mkdir()
    subprocess.run(['javac','-d',str(out),str(normalizer),str(harness)],check=True)
    subprocess.run(['java','-cp',str(out),'NormalizerSmokeTest'],check=True)
print('NOTE: This is a source/resource and pure-Java utility check, not an Android Gradle build or emulator test.')
