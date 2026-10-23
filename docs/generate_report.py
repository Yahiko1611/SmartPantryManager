from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_TAB_ALIGNMENT, WD_TAB_LEADER
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'SmartPantryManager_Report_Draft.docx'
DOCS = ROOT / 'docs'
EVIDENCE = DOCS / 'evidence'

doc = Document()
sec = doc.sections[0]
sec.top_margin = Inches(0.72)
sec.bottom_margin = Inches(0.72)
sec.left_margin = Inches(0.8)
sec.right_margin = Inches(0.8)

# Fixed assignment typography: Times New Roman 12 pt and 1.5 line spacing.
styles = doc.styles
for style_name in ['Normal', 'Title', 'Subtitle', 'Heading 1', 'Heading 2', 'Heading 3']:
    style = styles[style_name]
    style.font.name = 'Times New Roman'
    style.font.size = Pt(12)
    style.font.color.rgb = RGBColor(30, 45, 37)
    style._element.rPr.rFonts.set(qn('w:eastAsia'), 'Times New Roman')
    style.paragraph_format.line_spacing = 1.5
    style.paragraph_format.space_after = Pt(5)
    style.paragraph_format.space_before = Pt(3)
styles['Title'].font.bold = True
styles['Subtitle'].font.italic = True

# Footer with page field.
footer = sec.footer.paragraphs[0]
footer.alignment = WD_ALIGN_PARAGRAPH.CENTER
footer.paragraph_format.line_spacing = 1.5
run = footer.add_run('Smart Pantry Manager  |  Mobile App Development 700  |  Page ')
run.font.name = 'Times New Roman'; run.font.size = Pt(12)
fld = OxmlElement('w:fldSimple'); fld.set(qn('w:instr'), 'PAGE')
footer._p.append(fld)

def set_cell_shading(cell, fill):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = tcPr.find(qn('w:shd'))
    if shd is None:
        shd = OxmlElement('w:shd'); tcPr.append(shd)
    shd.set(qn('w:fill'), fill)

def set_cell_border(cell, color='DDE6DD', sz='8'):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    borders = tcPr.first_child_found_in('w:tcBorders')
    if borders is None:
        borders = OxmlElement('w:tcBorders'); tcPr.append(borders)
    for edge in ('top', 'left', 'bottom', 'right', 'insideH', 'insideV'):
        tag = 'w:' + edge
        element = borders.find(qn(tag))
        if element is None:
            element = OxmlElement(tag); borders.append(element)
        element.set(qn('w:val'), 'single')
        element.set(qn('w:sz'), sz)
        element.set(qn('w:color'), color)

def set_repeat_table_header(row):
    trPr = row._tr.get_or_add_trPr()
    tblHeader = OxmlElement('w:tblHeader')
    tblHeader.set(qn('w:val'), 'true')
    trPr.append(tblHeader)

def fmt_run(run, bold=False, italic=False, size=12, color=None, name='Times New Roman'):
    run.font.name = name
    run._element.get_or_add_rPr().rFonts.set(qn('w:eastAsia'), name)
    run.font.size = Pt(size)
    run.bold = bold
    run.italic = italic
    if color: run.font.color.rgb = RGBColor(*color)
    return run

def para(text='', bold=False, italic=False, align=None, keep=False):
    p = doc.add_paragraph()
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(5)
    p.paragraph_format.keep_with_next = keep
    if align is not None: p.alignment = align
    if text:
        fmt_run(p.add_run(text), bold=bold, italic=italic)
    return p

def heading(text, level=1):
    p = doc.add_paragraph(style=f'Heading {level}')
    p.paragraph_format.keep_with_next = True
    fmt_run(p.add_run(text), bold=True, size=12, color=(40,102,75))
    return p

def bullet(text):
    p = doc.add_paragraph(style='List Bullet')
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(3)
    fmt_run(p.add_run(text), size=12)
    return p

def numbered(text):
    p = doc.add_paragraph(style='List Number')
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(3)
    fmt_run(p.add_run(text), size=12)
    return p

def add_code_box(code):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = table.cell(0,0)
    set_cell_shading(cell, 'F2F5F1')
    set_cell_border(cell, 'C8D8CB', '6')
    trPr = table.rows[0]._tr.get_or_add_trPr()
    cant = OxmlElement('w:cantSplit'); trPr.append(cant)
    cell.text = ''
    p = cell.paragraphs[0]
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(2)
    for j, line in enumerate(code.strip('\n').split('\n')):
        if j > 0:
            p.add_run().add_break()
        r = p.add_run(line)
        fmt_run(r, size=12, name='Times New Roman')
    doc.add_paragraph()

def add_evidence_pair(left, right, page_num):
    heading(f'5.{page_num} Evidence: {left[0]} and {right[0]}', 2)
    table = doc.add_table(rows=1, cols=2)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    widths = [Inches(3.15), Inches(3.15)]
    for idx, (title, caption, image_name) in enumerate([left, right]):
        cell = table.cell(0,idx)
        cell.width = widths[idx]
        cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.TOP
        cell.text = ''
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.line_spacing = 1.5
        fmt_run(p.add_run(title), bold=True, size=12)
        image_path = EVIDENCE / image_name
        if image_path.is_file():
            picture = cell.add_paragraph()
            picture.alignment = WD_ALIGN_PARAGRAPH.CENTER
            picture.add_run().add_picture(str(image_path), width=Inches(2.25))
        else:
            set_cell_shading(cell, 'FFF7E8')
            set_cell_border(cell, 'D7B36A', '8')
            p2 = cell.add_paragraph()
            p2.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p2.paragraph_format.line_spacing = 1.5
            p2.paragraph_format.space_before = Pt(110)
            p2.paragraph_format.space_after = Pt(105)
            fmt_run(p2.add_run('SCREENSHOT NOT CAPTURED'), bold=True, size=12, color=(133,86,20))
            p3 = cell.add_paragraph()
            p3.alignment = WD_ALIGN_PARAGRAPH.CENTER
            fmt_run(p3.add_run(f'Capture: docs/evidence/{image_name}'), italic=True, size=10, color=(133,86,20))
        p4 = cell.add_paragraph()
        p4.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p4.paragraph_format.line_spacing = 1.2
        fmt_run(p4.add_run(caption), italic=True, size=10)
    row = table.rows[0]
    trPr = row._tr.get_or_add_trPr()
    h = OxmlElement('w:trHeight'); h.set(qn('w:val'), str(int(5.15*1440))); h.set(qn('w:hRule'),'atLeast'); trPr.append(h)
    para('Caption requirement: keep the final screenshot caption short and state exactly what the visible app state proves.', italic=True)

# Cover
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before = Pt(55)
fmt_run(p.add_run('RICHFIELD GRADUATE INSTITUTE OF TECHNOLOGY'), bold=True, size=12, color=(40,102,75))
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before = Pt(35)
fmt_run(p.add_run('FACULTY OF INFORMATION TECHNOLOGY'), bold=True, size=12)
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before = Pt(45)
fmt_run(p.add_run('MOBILE APP DEVELOPMENT 700'), bold=True, size=12)
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before = Pt(25)
fmt_run(p.add_run('PRACTICAL ASSIGNMENT'), bold=True, size=12)
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before = Pt(30)
fmt_run(p.add_run('SMART PANTRY MANAGER'), bold=True, size=12, color=(40,102,75))
p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
fmt_run(p.add_run('A Java Android application for strict recipe matching and food-waste reduction'), italic=True, size=12)
doc.add_paragraph()
t = doc.add_table(rows=6, cols=2); t.alignment = WD_TABLE_ALIGNMENT.CENTER; t.autofit = False
fields = [
    ('Name and Surname', '[Enter your full name]'),
    ('Student ITS No.', '[Enter your student number]'),
    ('Qualification / Year / Semester', '[Complete all applicable details]'),
    ('Assignment due date', '[Enter due date from Moodle]'),
    ('Date submitted', '[Complete on submission]'),
    ('GitHub repository', 'https://github.com/Yahiko1611/SmartPantryManager')
]
for i,(a,b) in enumerate(fields):
    t.cell(i,0).width=Inches(2.2); t.cell(i,1).width=Inches(4.1)
    for j,value in enumerate([a,b]):
        c=t.cell(i,j); c.text=''; set_cell_border(c,'DDE6DD','5')
        if j==0: set_cell_shading(c,'E5F1E8')
        c.vertical_alignment=WD_CELL_VERTICAL_ALIGNMENT.CENTER
        p=c.paragraphs[0]; p.paragraph_format.line_spacing=1.2
        fmt_run(p.add_run(value), bold=(j==0), size=12)
para('WORKING REPORT DRAFT — verify the claims and page numbers, enter your personal details, review and sign the declaration, and complete any remaining evidence before submission.', bold=True, align=WD_ALIGN_PARAGRAPH.CENTER)
doc.add_page_break()

# Declaration page
heading('Declaration of Originality',1)
para('I hereby declare that this assignment is my own work and has not been copied from any other source except where due acknowledgment is made. I affirm that all sources used have been properly cited and that this submission complies with the institution’s policies on academic integrity and plagiarism.')
para('Student Signature: _________________________________________________')
para('Date: __________________________')
para('Sign this declaration yourself only after checking that the submitted implementation, code explanation, report and recording are accurate and that you understand the work. Do not sign a blank or unreviewed statement.', italic=True)
doc.add_page_break()

# TOC
heading('Table of Contents',1)
contents = [
    ('1. Introduction', '4'),
    ('2. System Design', '4'),
    ('3. Implementation Overview', '6'),
    ('4. Key Code Snippets', '8'),
    ('5. Screenshots of Outputs and Core Functions', '11'),
    ('6. Challenges and Solutions', '18'),
    ('7. Conclusion and Reflection', '18'),
    ('8. References', '19')
]
for label, page in contents:
    p = doc.add_paragraph()
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(5)
    p.paragraph_format.tab_stops.add_tab_stop(Inches(6.25), WD_TAB_ALIGNMENT.RIGHT, WD_TAB_LEADER.DOTS)
    fmt_run(p.add_run(label), size=12)
    fmt_run(p.add_run('\t' + page), size=12)
para('Recheck page numbers after replacing the screenshot placeholders and before final submission.', italic=True)
doc.add_page_break()

# Introduction
heading('1. Introduction',1)
para('The Smart Pantry Manager is a Java-based Android application intended to help a household keep track of ingredients already available at home and find meals that can be prepared without buying additional ingredients. The practical assignment defines strict recipe matching as the core business rule: a recipe is suggested only when every required ingredient is present in the pantry in at least the required quantity (Richfield Graduate Institute of Technology, n.d.).')
para('The primary users are people who want a simple way to monitor pantry ingredients, quantities and optional expiry dates. The app provides an ingredient list, add/edit form, suggested-recipes list, recipe detail screen and settings screen. It is designed for direct use on a mobile screen, with focused screens and simple navigation, consistent with the module guide’s discussion of mobile interface design, Activities, Layouts and Intents (Iversen and Eierman, 2020).')
para('The implementation uses local SQLite storage so that pantry data is saved on the device and is available after the app is closed and reopened. The recipe catalogue is seeded on first database creation. The app does not use maps, GPS, location services, payments or grocery-store lookup because these features are explicitly outside the assignment scope (Richfield Graduate Institute of Technology, n.d.).')
para('Verification status as of 23 October 2026: the Android Gradle unit-test/build task and source/resource validator passed; lint completed with warnings. On an API 37 emulator, fresh database creation produced 20 recipes and 84 ingredient rows, and the app was exercised for pantry operations, persistence, strict matching, validation, Settings, expiry reminders and rotation. Ten genuine incremental audit commits are on the public repository branch. The scenario-by-scenario evidence and remaining checks are in docs/TESTING_CHECKLIST.md. This is still a draft: the student must verify the report, supply personal details and signature, write a truthful personal reflection, and record the narrated video.', italic=True)

heading('2. System Design',1)
heading('2.1 Screen flow',2)
para('Figure 1 shows the five required screens and the navigation paths. Android Intents pass the selected pantry-item ID to the add/edit screen and the selected recipe ID to the recipe detail screen. The navigation buttons provide access to the pantry, recipes and settings screens.')
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER
p.add_run().add_picture(str(DOCS/'screen_flow.png'), width=Inches(6.35))
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; fmt_run(p.add_run('Figure 1. Planned Smart Pantry Manager screen flow.'), italic=True, size=12)
heading('2.2 Database model',2)
para('The SQLite database contains three tables. `pantry_items` stores the user’s pantry records. `recipes` stores each recipe name, description and preparation instructions. `recipe_ingredients` stores one required ingredient, quantity and unit per row and links each requirement to its recipe through `recipe_id`. This keeps recipe requirements separate from pantry stock so the strict-matching logic can compare the two at runtime.')
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER
p.add_run().add_picture(str(DOCS/'database_erd.png'), width=Inches(6.35))
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; fmt_run(p.add_run('Figure 2. SQLite tables and their relationship.'), italic=True, size=12)
heading('2.3 Strict-matching rule',2)
para('The algorithm starts with the current pantry records and builds available totals using a normalised ingredient name and compatible unit family. The recipe catalogue is then evaluated one recipe at a time. Every ingredient required by the recipe is checked against the matching pantry total. If any ingredient is absent, has an incompatible measurement family or has less than the required amount, that recipe is excluded. Only recipes for which every requirement passes are returned to the Suggested Recipes screen. There is no partial-match recipe in the main suggestion list.')
para('Simple plurals and common measurement alternatives are normalised. Kilograms are converted to grams, litres to millilitres, and teaspoons/tablespoons/cups to millilitres. The app does not invent mass-to-volume or piece-to-weight conversions because those depend on the ingredient. During testing, use comparable units when entering an ingredient.')
heading('2.4 Interface and persistence choices',2)
para('The app uses XML layouts and RecyclerView with custom adapters for the two dynamic lists. SQLiteOpenHelper creates the schema and seeds the recipes. CRUD calls read and write pantry rows directly in SQLite, while SharedPreferences stores small settings such as the expiry-reminder toggle and default unit. These choices align with the module guide’s treatment of Activities, Layouts, Intents, adapters and persistent local data (Iversen and Eierman, 2020).')

heading('3. Implementation Overview',1)
heading('3.1 Required screens',2)
bullet('Pantry List (MainActivity): displays current pantry items from SQLite, shows quantity/unit/expiry, and provides Add, Edit and Delete actions.')
bullet('Add/Edit Ingredient (AddEditIngredientActivity): validates name, positive quantity and an optional YYYY-MM-DD expiry date before writing to SQLite.')
bullet('Suggested Recipes (SuggestedRecipesActivity): shows recipes returned by the strict-matching algorithm or a friendly empty-state message when none qualify.')
bullet('Recipe Detail (RecipeDetailActivity): displays the required ingredients, amounts, preparation method and description for the recipe selected using its ID.')
bullet('Settings (SettingsActivity): saves the expiry-alert preference and default quantity unit. The pantry screen uses the reminder setting to show a warning for items expiring within three days.')
heading('3.2 Recipe seed data',2)
para('The database seed method inserts 20 recipes and their required ingredients the first time the database is created. Examples include Tomato Egg Scramble, Garlic Tomato Pasta, Banana Oat Pancakes, Vegetable Fried Rice, Chickpea Salad and Berry Yogurt Smoothie. The pantry itself begins empty so the first suggested-recipes result is expected to show the no-match state until enough ingredients are added.')
heading('3.3 Input validation',2)
para('The ingredient form rejects an empty name, a missing or non-numeric quantity, a quantity less than or equal to zero, and a malformed expiry date. The expiry date can be omitted. Input validation is important because the quantity is used directly in the matching comparison; invalid values could create misleading suggestions.')
heading('3.4 Test status',2)
para('The project was built and its 16 strict-matching unit tests passed. Source/resource validation also passed; Android lint completed with 98 warnings. On an API 37 emulator, first-run recipe seeding, persistence, pantry add/edit/delete, zero/negative quantity validation, expiry alerts, strict matching and rotation were observed. Non-numeric quantity entry, plural-name matching and unit-conversion UI cases remain unverified. See docs/TESTING_CHECKLIST.md for the detailed results; do not treat this draft as a complete test report.')

doc.add_page_break()
heading('4. Key Code Snippets',1)
para('The following short excerpts are taken from the current project source. The explanations describe their intended role; check them against the final code version that is built and recorded for submission.')
heading('4.1 SQLite create operation',2)
add_code_box('''public long insertPantryItem(String name, double quantity, String unit, String expiryDate) {
    ContentValues values = new ContentValues();
    values.put("name", name.trim());
    values.put("quantity", quantity);
    values.put("unit", IngredientNormalizer.canonicalUnit(unit));
    if (expiryDate == null || expiryDate.trim().isEmpty()) {
        values.putNull("expiry_date");
    } else {
        values.put("expiry_date", expiryDate.trim());
    }
    return getWritableDatabase().insertOrThrow("pantry_items", null, values);
}''')
para('Explanation: the method builds ContentValues for a new pantry row and inserts it into the SQLite table. It trims the name, normalises the unit and stores a blank expiry date as SQL NULL. The companion read, update and delete methods complete the CRUD set.')
doc.add_page_break()
heading('4.2 Strict matching',2)
add_code_box('''public static boolean canMake(Map<String, Double> available, List<Line> required) {
    if (required.isEmpty()) return false;
    for (Line need : required) {
        IngredientNormalizer.Measure m =
            IngredientNormalizer.toMeasure(need.quantity, need.unit);
        String key = IngredientNormalizer.normalizeName(need.name) + "|" + m.family;
        Double have = available.get(key);
        if (have == null || have + 0.000001 < m.baseQuantity) return false;
    }
    return true;
}''')
para('Explanation: RecipeMatcher.canMake checks each required ingredient against available pantry stock using normalised names and compatible measurement families. It rejects an empty requirement list and returns false as soon as any ingredient is missing or insufficient, so partial matches are not suggested.')
doc.add_page_break()
heading('4.3 Quantity and unit normalisation',2)
add_code_box('''public static Measure toMeasure(double quantity, String rawUnit) {
    String unit = canonicalUnit(rawUnit);
    switch (unit) {
        case "kg": return new Measure("mass", quantity * 1000.0, "g");
        case "g":  return new Measure("mass", quantity, "g");
        case "l":  return new Measure("volume", quantity * 1000.0, "ml");
        case "ml": return new Measure("volume", quantity, "ml");
        case "tsp": return new Measure("volume", quantity * 5.0, "ml");
        case "tbsp": return new Measure("volume", quantity * 15.0, "ml");
        default: return new Measure("unit:" + unit, quantity, unit);
    }
}''')
para('Explanation: compatible units are converted to a comparable base quantity before matching. Separate measurement families avoid treating grams as equivalent to millilitres or pieces. The final submitted code and video explanation should reflect the exact conversion rules implemented.')
doc.add_page_break()
heading('4.4 Intent and custom adapter',2)
add_code_box('''Intent intent = new Intent(this, AddEditIngredientActivity.class);
intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
startActivity(intent);

// In PantryAdapter.onBindViewHolder(...)
holder.name.setText(item.getName());
holder.quantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());
holder.edit.setOnClickListener(v -> listener.onEdit(item));
holder.delete.setOnClickListener(v -> listener.onDelete(item));''')
para('Explanation: the Intent carries the selected database ID rather than copying the entire record between screens. The edit Activity retrieves the current item from SQLite. The custom adapter binds each record into its row and forwards Edit/Delete actions to the Activity.')

heading('5. Screenshots of Outputs and Core Functions',1)
para('The evidence images are direct captures from the running API 37 emulator. Each caption states the visible behavior. Review each image against the final app version and replace any screenshot that does not meet the lecturer’s requirements.')
evidence = [
    ('Pantry list with saved ingredients','Shows saved records, units and quantities.','pantry_list.png'),
    ('Add ingredient form','Shows the name, quantity, unit and optional expiry controls.','add_form.png'),
    ('Validation error','Shows the form response to an invalid save.','validation_error.png'),
    ('Edit ingredient form','Shows an existing pantry item loaded for editing.','edit_form.png'),
    ('Pantry after update','Shows saved values after editing.','pantry_after_update.png'),
    ('Delete confirmation','Shows the prompt before deleting an item.','delete_confirmation.png'),
    ('Pantry after deletion','Shows the item removed from the pantry.','pantry_after_deletion.png'),
    ('Strict match included','Shows a recipe when all requirements are covered.','strict_match_included.png'),
    ('Strict match excluded','Shows the no-match state after a required ingredient is removed.','strict_match_excluded.png'),
    ('Recipe detail screen','Shows the recipe requirements and preparation steps.','recipe_detail.png'),
    ('Settings screen','Shows the saved alert and default-unit preferences.','settings.png'),
    ('Expiry reminder','Shows the pantry reminder for an item expiring within three days.','expiry_reminder.png'),
    ('Fractional quantity display','Shows that `0.001 kg` is displayed accurately.','fractional_quantity.png'),
    ('Persistence after relaunch','Shows saved pantry records after restarting the app.','persistence_relaunch.png')
]
for k in range(0,len(evidence),2):
    doc.add_page_break()
    a=evidence[k]; b=evidence[k+1] if k+1 < len(evidence) else ('Additional required screen','Insert any remaining core screen or function evidence.','additional.png')
    add_evidence_pair(a,b,1+(k//2))

doc.add_page_break()
heading('6. Challenges and Solutions',1)
para('This section must describe two or three real problems that arose during your own build/test process and how you personally resolved them. The options below are technical issues to consider only if you actually encounter, investigate and verify them; edit them to match what happened in your environment.')
heading('Challenge 1 — Strict matching rather than partial matching',2)
para('Potential solution to document if verified: check every required ingredient in a recipe and stop evaluating that recipe as soon as one requirement is absent or insufficient. A test should remove a single requirement and confirm that the recipe disappears from suggestions.')
heading('Challenge 2 — Ingredient naming and compatible units',2)
para('Potential solution to document if verified: normalise common singular/plural variants and convert compatible units such as kg/g and L/ml to a base unit before quantity comparison. Do not invent conversions between units that cannot safely be compared without ingredient-specific data.')
heading('Challenge 3 — Local persistence and returning to a screen',2)
para('Potential solution to document if verified: use SQLite as the source of truth and reload records in the Activities’ onResume lifecycle callback. Test by changing a pantry item, navigating away, returning and restarting the application.')
para('Before final submission, replace at least two of the above candidate descriptions with the actual issue, evidence/log observed, code or configuration change made, and result of the retest. If a challenge did not occur, do not claim it did.')

heading('7. Conclusion and Reflection',1)
para('The Smart Pantry Manager is designed to combine local data persistence, dynamic list displays, Activity navigation, form validation and recipe-matching business logic in a small Android application. Its key design principle is that the Suggested Recipes screen should represent meals that are genuinely possible with the current pantry, not meals that are merely close to possible. This makes the quantity comparison and the exclusion of partial matches central to the system.')
para('Before submitting, add a brief personal reflection based on the work you actually completed: what you learned about SQLite, custom adapters, Intents or the Activity lifecycle; what you personally found most difficult; and one improvement you would implement with more time. Possible future enhancements include ingredient-specific mass/volume conversions, better expiry filtering, recipe search and automated unit tests, but any enhancement must preserve the strict-matching rule.')

heading('8. References',1)
para('Iversen, J. and Eierman, M. (2020) Mobile App Development for iOS and Android. 3rd edn. Prospect Press.')
para('Richfield Graduate Institute of Technology (n.d.) Mobile App Development 700: Practical Assignment — Smart Pantry Manager. Practical assignment brief supplied on the module.')
para('Check whether your lecturer expects additional references for any external tutorial, code or documentation you consult while testing or changing the project. Add those sources in Harvard style and cite them in the relevant report section. Do not list sources you did not use.')

# Document metadata
doc.core_properties.title = 'Smart Pantry Manager - Mobile App Development 700 Practical Assignment Report Draft'
doc.core_properties.subject = 'Java Android application using SQLite strict recipe matching'
doc.core_properties.author = '[Student name to be entered]'
# Remove Markdown inline-code delimiters so they do not appear as literal backticks in Word.
def strip_backticks_from_content(container):
    for paragraph in container.paragraphs:
        for run in paragraph.runs:
            if '`' in run.text:
                run.text = run.text.replace('`', '')
    for table in container.tables:
        for row in table.rows:
            for cell in row.cells:
                strip_backticks_from_content(cell)
strip_backticks_from_content(doc)

doc.save(OUT)
print(OUT)
