# Design System — Theme Reference

This document is the **single source of truth** for the visual design of Random Task. Every UI element — screens, components, dialogs, badges, buttons, inputs, empty states — must be built for **all three themes** using the tokens and patterns defined here.

**When to read this file:** Any time you are creating, modifying, or reviewing a UI element. Every composable that renders visible pixels must conform to this spec.

---

## The Three Themes

| Theme | ID | Philosophy | Mode |
|-------|-----|------------|------|
| **Obsidian** | `OBSIDIAN` | Sleek, dark, focused. Teal accent on pure black. Gradient cards with priority edge accents. Feels like a premium instrument panel. | Dark |
| **Neo Brutalist** | `NEO_BRUTALIST` | Bold, loud, unapologetic. Thick borders, offset drop shadows, punchy color blocks. High contrast, high energy. | Light |
| **Vapor** | `VAPOR` | Soft, calming, pastel-tinted dark mode. Priority determines card tint. Rounded shapes, gentle gradients. Wellness app aesthetic. | Dark |

The user selects a theme in Settings. The selection is persisted via DataStore. All screens must render correctly in all three themes.

---

## 1. Color Palettes

### 1.1 Obsidian

| Token | Hex | Usage |
|-------|-----|-------|
| `background` | `#000000` | Screen background, scaffold |
| `card` | `#1A1A1A` | Card/surface fill (gradient start) |
| `cardHighlight` | `#242424` | Card gradient end |
| `primary` | `#00BFA5` | Primary accent — FAB, active checkbox, interactive highlights |
| `primaryDim` | `#00897B` | Dimmed accent — nav icons, category text, secondary interactive |
| `textPrimary` | `#E8E8E8` | Main body text, titles |
| `textSecondary` | `#8A8A8A` | Metadata, subtitles, placeholders, disabled text |
| `error` | `#FF5252` | Error states, overdue dates, high priority indicator |
| `priorityHigh` | `#FF5252` | High priority dot/bar/badge |
| `priorityMedium` | `#FFB74D` | Medium priority dot/bar/badge |
| `priorityLow` | `#69F0AE` | Low priority dot/bar/badge |
| `onPrimary` | `#000000` | Text/icon on primary-colored surfaces (FAB icon, checkbox mark) |

### 1.2 Neo Brutalist

| Token | Hex | Usage |
|-------|-----|-------|
| `background` | `#F5F0EB` | Screen background — warm off-white |
| `card` | `#FFFFFF` | Card fill |
| `border` | `#1A1A1A` | All borders and shadow fills |
| `shadow` | `#1A1A1A` | Offset drop shadow (solid, not blur) |
| `textPrimary` | `#1A1A1A` | All primary text, icons, headers |
| `textSecondary` | `#6B7280` | Metadata, disabled text, due dates |
| `accentPink` | `#FF2D78` | Primary action (FAB), high priority, overdue, error |
| `accentYellow` | `#FFE500` | Medium priority, highlights, callout backgrounds |
| `accentBlue` | `#3B82F6` | Category labels, links, informational |
| `accentGreen` | `#22C55E` | Low priority, success states |
| `priorityHigh` | `#FF2D78` | High priority badge bg (text: white) |
| `priorityMedium` | `#FFE500` | Medium priority badge bg (text: `#1A1A1A`) |
| `priorityLow` | `#22C55E` | Low priority badge bg (text: white) |
| `onAccentPink` | `#FFFFFF` | Text on pink surfaces |
| `onAccentYellow` | `#1A1A1A` | Text on yellow surfaces |
| `onAccentGreen` | `#FFFFFF` | Text on green surfaces |

### 1.3 Vapor

| Token | Hex | Usage |
|-------|-----|-------|
| `background` | `#0E1118` | Screen background — deep blue-gray |
| `card` | `#171C28` | Neutral card fill (stats, non-priority surfaces) |
| `textPrimary` | `#E2E8F0` | Main body text |
| `textSecondary` | `#64748B` | Metadata, subtitles, placeholders |
| `accentPink` | `#F9A8D4` | High priority accent, FAB, emphasis |
| `accentPinkDim` | `#9D4E7C` | Dimmed pink — secondary interactive |
| `accentTeal` | `#5EEAD4` | Low priority accent, success, positive |
| `accentTealDim` | `#2D8B7A` | Dimmed teal — secondary interactive |
| `accentIndigo` | `#A5B4FC` | Medium priority accent |
| `accentCream` | `#FEF3C7` | Warm accent (tertiary, optional highlights) |
| `error` | `#FCA5A5` | Error, overdue dates |
| **Priority card tints** | | |
| `priorityHighBg` | `#1F1520` | Card background for HIGH priority items |
| `priorityHighAccent` | `#F9A8D4` | Text/icon accent on high priority cards |
| `priorityMediumBg` | `#15182A` | Card background for MEDIUM priority items |
| `priorityMediumAccent` | `#A5B4FC` | Text/icon accent on medium priority cards |
| `priorityLowBg` | `#101D1A` | Card background for LOW priority items |
| `priorityLowAccent` | `#5EEAD4` | Text/icon accent on low priority cards |
| `onAccentPink` | `#0E1118` | Text on pink surfaces (FAB icon, filled checkbox) |

---

## 2. Typography

All three themes use `FontFamily.Default` (system sans-serif). They differ in weight emphasis and casing.

### 2.1 Text Style Matrix

| Role | Obsidian | Neo Brutalist | Vapor |
|------|----------|---------------|-------|
| **Screen title** | 32sp, Bold | 36sp, Black, UPPERCASE, letterSpacing -1sp | 28sp, SemiBold |
| **Screen subtitle** | 14sp, Normal, `textSecondary` | *(none — uses thick underline instead)* | 13sp, Normal, `accentTeal` @ 50% alpha, letterSpacing 1sp |
| **Card title** | 16sp, Medium | 16sp, Bold | 15sp, Medium |
| **Card metadata** | 12sp, Normal | 12sp, SemiBold | 12sp, Normal |
| **Category label** | 12sp, Normal, `primaryDim` | 11sp, Bold, UPPERCASE, `accentBlue`, letterSpacing 1sp | 12sp, Normal, accent @ 50% alpha |
| **Priority badge** | *(dot only, no text)* | 10sp, Black, UPPERCASE, letterSpacing 1sp | 10sp, SemiBold |
| **Empty state title** | 22sp, SemiBold | 28sp, Black, UPPERCASE | 24sp, Light |
| **Empty state body** | 14sp, Normal, 60% alpha | 14sp, Bold, UPPERCASE | 13sp, Normal, accent @ 40% alpha |
| **Stat value** | *(N/A)* | *(N/A)* | 20sp, Bold |
| **Stat label** | *(N/A)* | *(N/A)* | 11sp, Normal, `textSecondary` |

### 2.2 Text Casing Rules

- **Obsidian:** Sentence case everywhere. No uppercase transforms.
- **Neo Brutalist:** UPPERCASE for screen titles, priority badges, category labels, empty state text. Sentence case for card titles and metadata.
- **Vapor:** Sentence case everywhere. No uppercase transforms.

### 2.3 Completed Task Text Treatment

All themes: `TextDecoration.LineThrough` with dimmed color (`textSecondary`).

---

## 3. Shapes & Geometry

### 3.1 Corner Radii

| Element | Obsidian | Neo Brutalist | Vapor |
|---------|----------|---------------|-------|
| **Cards** | 20dp | 12dp | 22dp |
| **Buttons / FAB** | CircleShape | 12dp (with border) | 20dp |
| **Checkbox** | CircleShape (24dp) | 6dp rounded rect (28dp) | 8dp rounded rect (24dp) |
| **Priority badge** | CircleShape (10dp dot) | 4dp (filled block badge with border) | 10dp pill |
| **Dialogs** | 20dp | 12dp (with border) | 22dp |
| **Input fields** | 16dp | 8dp (with border) | 16dp |
| **Stat pills** | *(N/A)* | *(N/A)* | 16dp |

### 3.2 Borders

| Element | Obsidian | Neo Brutalist | Vapor |
|---------|----------|---------------|-------|
| **Card border** | None (uses gradient fill) | **3dp solid `#1A1A1A`** | None |
| **Checkbox border** | 2dp stroke `textSecondary` (unchecked) | **3dp solid `#1A1A1A`** | None (uses alpha fill) |
| **Priority badge border** | None | **2dp solid `#1A1A1A`** | None |
| **FAB border** | None | **3dp solid `#1A1A1A`** | None |
| **Input field border** | 1dp `textSecondary` | **3dp solid `#1A1A1A`** | 1dp `textSecondary` |
| **Dialog border** | None | **3dp solid `#1A1A1A`** | None |

**Neo Brutalist special rule:** Almost everything has a thick `#1A1A1A` border. When in doubt, add a border.

### 3.3 Shadows

| Theme | Shadow Type | Implementation |
|-------|-------------|----------------|
| **Obsidian** | None | Cards use gradient fills for depth |
| **Neo Brutalist** | **Offset solid shadow** | Separate `Box` behind the element, offset `+4dp x, +4dp y`, filled `#1A1A1A`, same shape. Uses `matchParentSize()` + `offset()` pattern. |
| **Vapor** | None | Cards use subtle background tint for depth |

**Neo Brutalist shadow pattern (Compose):**
```kotlin
Box {
    // Shadow layer
    Box(
        modifier = Modifier
            .matchParentSize()
            .offset(x = 4.dp, y = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
    )
    // Actual content
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(3.dp, Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
    ) {
        // content here
    }
}
```

---

## 4. Spacing

### 4.1 Consistent Values (all themes)

| Token | Value | Usage |
|-------|-------|-------|
| `screenPaddingH` | 20dp | Horizontal padding for screen content (Obsidian, Vapor); 20dp (Neo Brutalist) |
| `screenPaddingV` | 16–20dp | Vertical padding for headers |
| `cardPadding` | 16dp | Internal card padding (all themes) |
| `cardGap` | 12dp (Obsidian), 16dp (Neo Brutalist), 10dp (Vapor) | Vertical spacing between cards in lists |
| `elementGap` | 8dp | Spacing between inline elements (icon to text, badge to text) |
| `metadataGap` | 4dp | Vertical spacing between title and metadata row |
| `listBottomSpacer` | 80dp | Extra space at bottom of lists (FAB clearance) |

### 4.2 Card Internal Layout

All themes use the same structural layout inside a card:
```
[Checkbox] — 14-16dp gap — [Title + Metadata column (weight 1f)] — [Priority indicator]
```

---

## 5. Component Patterns

### 5.1 Screen Scaffold

Every screen follows this structure:

```
Scaffold(
    containerColor = theme.background,
    floatingActionButton = { ThemeFAB(...) },
    snackbarHost = { SnackbarHost(...) },
) { innerPadding ->
    Column(modifier.fillMaxSize().padding(innerPadding).background(theme.background)) {
        ThemeHeader(...)
        // Content
    }
}
```

### 5.2 Headers

| Part | Obsidian | Neo Brutalist | Vapor |
|------|----------|---------------|-------|
| Title | Large text, left-aligned | Large UPPERCASE text, left-aligned | Medium text, left-aligned |
| Subtitle | Below title, `textSecondary` | 4dp thick full-width underline below title | Below title, tinted accent |
| Nav icons | Right-aligned row, `primaryDim` tint | Right-aligned row, `textPrimary` tint | Right-aligned row, mixed dim tints |

### 5.3 Cards

**Obsidian Card:**
- `RoundedCornerShape(20dp)`
- Fill: `Brush.horizontalGradient(card, cardHighlight)`
- Left accent bar: 4dp wide, priority color, drawn via `drawBehind`
- No border, no shadow

**Neo Brutalist Card:**
- `RoundedCornerShape(12dp)`
- Fill: white `#FFFFFF`
- Border: 3dp solid `#1A1A1A`
- Shadow: offset solid `#1A1A1A` box at +4dp x/y behind
- Priority shown as filled color block badge with border

**Vapor Card:**
- `RoundedCornerShape(22dp)`
- Fill: **priority-tinted background** (`priorityHighBg` / `priorityMediumBg` / `priorityLowBg`)
- No border, no shadow
- Priority shown as pastel pill with matching accent text

### 5.4 Checkboxes

**Obsidian:** 24dp circle. Unchecked: transparent with 2dp `textSecondary` stroke. Checked: filled `primary` with "✓" in `onPrimary`.

**Neo Brutalist:** 28dp rounded rect (6dp radius). Unchecked: transparent with 3dp `border` stroke. Checked: filled `textPrimary` with "✓" in white, Black weight.

**Vapor:** 24dp rounded rect (8dp radius). Unchecked: priority accent at 15%→8% alpha gradient. Checked: priority accent gradient fill with "✓" in `background`.

### 5.5 Priority Indicators

**Obsidian:** 10dp circle dot filled with priority color. Also: 4dp left accent bar on cards.

**Neo Brutalist:** Block badge with priority color fill, 2dp `border` stroke, UPPERCASE text (10sp, Black weight). Text colors vary: white on pink/green, black on yellow.

**Vapor:** Pill badge (10dp radius), priority accent text on 10% alpha priority accent background. 10sp SemiBold.

### 5.6 FAB (Floating Action Button)

**Obsidian:** `CircleShape`, filled `primary`, icon in `onPrimary`.

**Neo Brutalist:** `RoundedCornerShape(12dp)`, filled `accentPink`, icon in white, 3dp `border` stroke, offset shadow behind.

**Vapor:** `RoundedCornerShape(20dp)`, filled `accentPink`, icon in `background`.

### 5.7 Empty States

**Obsidian:** Centered column. Title: 22sp SemiBold `textSecondary`. Body: 14sp `textSecondary` at 60% alpha.

**Neo Brutalist:** Centered column. Title: 28sp Black UPPERCASE `textPrimary`. Body: inside a yellow badge with border — 14sp Bold `textPrimary`.

**Vapor:** Centered column. Title: 24sp Light `textSecondary`. Body: 13sp `accentTeal` at 40% alpha.

### 5.8 Loading States

**Obsidian:** `CircularProgressIndicator` in `primary`.

**Neo Brutalist:** `CircularProgressIndicator` in `accentPink`, strokeWidth 4dp.

**Vapor:** `CircularProgressIndicator` in `accentTeal`, strokeWidth 2dp.

### 5.9 Dialogs

Apply the same surface/border/shadow rules as cards:

**Obsidian:** Dark surface (`card` color), `primary` accent for buttons, 20dp rounded corners.

**Neo Brutalist:** White surface, 3dp black border, offset shadow, 12dp rounded corners. Buttons use accent colors with borders.

**Vapor:** Deep card surface (`card` color), pastel accent buttons, 22dp rounded corners.

### 5.10 Text Inputs / Search Fields

**Obsidian:** Dark fill (`card`), light text, `primary` cursor/focus indicator, 16dp rounded corners.

**Neo Brutalist:** White fill, 3dp black border, 8dp rounded corners. Bold placeholder text.

**Vapor:** Dark fill (`card`), light text, pastel focus indicator, 16dp rounded corners.

### 5.11 Filter Chips / Segmented Controls

**Obsidian:** Outlined in `primaryDim`, selected fills `primary` with `onPrimary` text.

**Neo Brutalist:** Black border, selected fills with accent color + border. Bold label text.

**Vapor:** Pastel-tinted fill matching priority colors. Selected has stronger alpha.

### 5.12 Snackbars

**Obsidian:** Dark surface, `primary` action text.

**Neo Brutalist:** White surface, black border, `accentPink` action text.

**Vapor:** Dark surface (`card`), `accentPink` action text.

### 5.13 Stat Pills (Vapor only)

Vapor uses a row of stat pills at the top of list screens:
- `RoundedCornerShape(16dp)`
- Fill: context-appropriate background (neutral `card`, or priority tint)
- Value: 20sp Bold in accent color
- Label: 11sp Normal in `textSecondary`
- Layout: equal-weight row of 3 pills

Other themes do not use stat pills. If a screen needs summary stats in Obsidian or Neo Brutalist, use a different pattern appropriate to that theme (e.g., Obsidian could use a gradient hero card, Neo Brutalist could use a bold text block).

### 5.14 Dividers / Separators

**Obsidian:** No dividers — cards are separated by spacing.

**Neo Brutalist:** 4dp thick black bars for section separators. No thin dividers between cards (spacing + shadow handles separation).

**Vapor:** No dividers — cards are separated by spacing.

---

## 6. State Handling

### 6.1 Completed Items

| Aspect | All Themes |
|--------|------------|
| Title text | `TextDecoration.LineThrough`, `textSecondary` color |
| Checkbox | Filled state (theme-specific fill color) |
| Card opacity | Normal (no alpha reduction on the card itself) |
| Priority indicator | Still shown but dimmed in context |

### 6.2 Overdue Items

| Aspect | Obsidian | Neo Brutalist | Vapor |
|--------|----------|---------------|-------|
| Due date text color | `#FF5252` | `#FF2D78` | `#FCA5A5` |

### 6.3 Disabled / Inactive

- Use `textSecondary` at 50% alpha for disabled text
- Reduce interactive element alpha to 38% (Material guideline)

---

## 7. Icon Usage

All themes use Material Icons (filled style). Icon sizes:

| Context | Size |
|---------|------|
| Navigation bar icons | 24dp |
| Card action icons (edit, delete) | 24dp |
| FAB icon | 24dp (default) |
| Small inline icons | 18dp |

Icon tinting follows the theme's icon color tokens (see Headers section for nav icon tints).

---

## 8. Implementation Checklist

When building **any** new UI element:

1. Define the element for all three themes in a `when(theme)` block or via theme-aware composables
2. Use the color tokens from Section 1 — never hardcode hex values outside of theme definitions
3. Follow the shape rules from Section 3 — especially Neo Brutalist's border and shadow requirements
4. Follow the typography rules from Section 2 — especially Neo Brutalist's UPPERCASE and weight rules
5. Handle all states: default, completed, overdue, loading, empty, error, disabled
6. Ensure the element works on both the lightest surface (Neo Brutalist `#F5F0EB`) and darkest surface (Obsidian `#000000`)
7. Test with Compose Preview in all three themes before considering the element done

---

## 9. Mapping to Material3

When the themes are implemented as proper `ColorScheme` objects:

| Material3 Role | Obsidian | Neo Brutalist | Vapor |
|----------------|----------|---------------|-------|
| `primary` | `#00BFA5` | `#FF2D78` | `#F9A8D4` |
| `onPrimary` | `#000000` | `#FFFFFF` | `#0E1118` |
| `background` | `#000000` | `#F5F0EB` | `#0E1118` |
| `surface` | `#1A1A1A` | `#FFFFFF` | `#171C28` |
| `onSurface` | `#E8E8E8` | `#1A1A1A` | `#E2E8F0` |
| `onSurfaceVariant` | `#8A8A8A` | `#6B7280` | `#64748B` |
| `error` | `#FF5252` | `#FF2D78` | `#FCA5A5` |
| `surfaceVariant` | `#242424` | `#F5F0EB` | `#171C28` |
| `outline` | `#8A8A8A` | `#1A1A1A` | `#64748B` |

**Custom extensions needed** (via `CompositionLocal` or theme data class):

- `priorityHigh`, `priorityMedium`, `priorityLow` (color)
- `priorityHighBg`, `priorityMediumBg`, `priorityLowBg` (Vapor card tints)
- `borderWidth` (0dp for Obsidian/Vapor, 3dp for Neo Brutalist)
- `shadowOffset` (0dp for Obsidian/Vapor, 4dp for Neo Brutalist)
- `useSolidShadow` (false/true/false)
- `useUppercaseTitles` (false/true/false)
