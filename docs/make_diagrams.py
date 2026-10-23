from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

OUT = Path(__file__).resolve().parent
W, H = 1800, 1000
BG = '#F3F7F1'; GREEN = '#28664B'; DARK = '#1E2D25'; MUTED = '#647168'; WHITE = '#FFFFFF'; BORDER = '#DDE6DD'; PALE = '#E5F1E8'; AMBER = '#FFF1D6'

def font(size, bold=False):
    options = [
        '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf' if bold else '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',
        '/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf' if bold else '/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf'
    ]
    for p in options:
        if Path(p).exists(): return ImageFont.truetype(p, size)
    return ImageFont.load_default()

def center_text(draw, box, lines, fnt, fill):
    x1,y1,x2,y2 = box
    lines = lines.split('\n')
    heights = [draw.textbbox((0,0), t, font=fnt)[3] - draw.textbbox((0,0), t, font=fnt)[1] for t in lines]
    total = sum(heights) + (len(lines)-1)*10
    y = y1 + (y2-y1-total)/2
    for text, ht in zip(lines, heights):
        bb = draw.textbbox((0,0), text, font=fnt)
        tw = bb[2]-bb[0]
        draw.text((x1+(x2-x1-tw)/2, y), text, font=fnt, fill=fill)
        y += ht + 10

def arrow(draw, start, end, fill=GREEN, width=6, head=20):
    draw.line([start,end], fill=fill, width=width)
    import math
    ang = math.atan2(end[1]-start[1], end[0]-start[0])
    a1 = ang + math.radians(150); a2 = ang - math.radians(150)
    p1=(end[0]+head*math.cos(a1), end[1]+head*math.sin(a1))
    p2=(end[0]+head*math.cos(a2), end[1]+head*math.sin(a2))
    draw.polygon([end,p1,p2], fill=fill)

# Screen flow diagram
im=Image.new('RGB',(W,H),BG); d=ImageDraw.Draw(im)
d.text((70,45),'Smart Pantry Manager — Screen Flow',font=font(38,True),fill=DARK)
d.text((70,105),'Intent-based navigation between the required Activities',font=font(23),fill=MUTED)
boxes={
 'pantry':(100,230,580,390),
 'edit':(660,230,1120,390),
 'recipes':(660,535,1120,695),
 'detail':(1250,535,1710,695),
 'settings':(100,535,580,695)
}
labels={'pantry':'Pantry List\nMainActivity','edit':'Add / Edit Ingredient\nAddEditIngredientActivity','recipes':'Suggested Recipes\nSuggestedRecipesActivity','detail':'Recipe Detail\nRecipeDetailActivity','settings':'Settings\nSettingsActivity'}
for key,box in boxes.items():
    d.rounded_rectangle(box, radius=28, fill=WHITE, outline=GREEN if key=='pantry' else BORDER, width=5 if key=='pantry' else 3)
    center_text(d,box,labels[key],font(27,True) if key in ('pantry','recipes','detail') else font(24,True),DARK)
arrow(d,(580,310),(660,310))
arrow(d,(410,390),(410,535))
arrow(d,(545,380),(690,535))
arrow(d,(1120,615),(1250,615))
# navigation note
note=(600,800,1220,920)
d.rounded_rectangle(note,radius=20,fill=PALE,outline=BORDER,width=2)
center_text(d,note,'Persistent bottom navigation\nPantry  •  Recipes  •  Settings',font(24,True),GREEN)
d.text((85,830),'Pantry item ID is passed\nthrough the edit Intent.',font=font(20),fill=MUTED)
d.text((1290,820),'Recipe ID is passed\nto the detail Activity.',font=font(20),fill=MUTED)
im.save(OUT/'screen_flow.png',quality=95)

# Database model ERD
im=Image.new('RGB',(W,H),BG); d=ImageDraw.Draw(im)
d.text((70,45),'Smart Pantry Manager — Data Model (SQLite)',font=font(38,True),fill=DARK)
d.text((70,105),'Three tables; recipe_ingredients stores each recipe’s required quantities.',font=font(23),fill=MUTED)

def table(box,title,fields):
    x1,y1,x2,y2=box
    d.rounded_rectangle(box,radius=20,fill=WHITE,outline=GREEN,width=4)
    d.rounded_rectangle((x1,y1,x2,y1+68),radius=20,fill=PALE,outline=GREEN,width=2)
    d.rectangle((x1,y1+38,x2,y1+68),fill=PALE)
    tw=d.textbbox((0,0),title,font=font(28,True))[2]
    d.text((x1+(x2-x1-tw)/2,y1+16),title,font=font(28,True),fill=GREEN)
    y=y1+88
    for field in fields:
        d.text((x1+24,y),field,font=font(22),fill=DARK)
        y+=43

pantry=(100,280,610,690)
recipes=(755,200,1255,505)
ri=(1330,280,1720,690)
table(pantry,'pantry_items',['PK id: INTEGER','name: TEXT','quantity: REAL > 0','unit: TEXT','expiry_date: TEXT, nullable','created_at: TEXT'])
table(recipes,'recipes',['PK id: INTEGER','name: TEXT, unique','description: TEXT','instructions: TEXT'])
table(ri,'recipe_ingredients',['PK id: INTEGER','FK recipe_id: INTEGER','ingredient_name: TEXT','required_quantity: REAL','unit: TEXT'])
# relationship from recipes to recipe ingredients
arrow(d,(1255,355),(1330,355))
d.text((1245,315),'1',font=font(24,True),fill=GREEN)
d.text((1292,315),'many',font=font(21,True),fill=GREEN)
# legend/logic
note=(630,600,1260,855)
d.rounded_rectangle(note,radius=20,fill=PALE,outline=BORDER,width=2)
center_text(d,note,'Runtime matching\nNormalise ingredient names, convert compatible units,\nand compare available quantity with each requirement.\nPantry entries are not foreign-key-linked to recipes.',font(22),DARK)
d.text((110,755),'Pantry data persists locally.',font=font(23,True),fill=GREEN)
im.save(OUT/'database_erd.png',quality=95)
