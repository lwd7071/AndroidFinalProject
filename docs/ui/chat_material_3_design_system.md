---
name: "Chat — Material 3 Design System"
description: "Calm, content-first native Android Material 3 design system for a Vietnamese offline-first messaging app."
---

## Overview

**Chat** is a native Android Material 3 messaging system designed for a four-person university project with the polish of a practical everyday product. The interface is calm, legible, and content-first, prioritizing rapid message scanning, reliable offline-first status clarity, and comfortable one-handed mobile ergonomics.

- **Style:** Native Android Material 3 (flat tonal surfaces, crisp typography, generous breathing room).
- **Audience:** Vietnamese university students and everyday Android users communicating in 1-to-1 and group threads.
- **Explicit Constraints (Do's and Don'ts):**
  - **Do** use flat conversation lists with 16px horizontal mobile margins and at least 48×48px touch targets.
  - **Do** pair every status (Sending, Sent, Failed, Unread, Offline) with an icon or text label—never rely on color alone.
  - **Do** support Vietnamese diacritics cleanly with Roboto and allow long names to wrap or ellipsize gracefully without clipping.
  - **Don't** use glassmorphism (`backdrop-blur`), decorative gradients, heavy drop shadows, or nested cards.

## Colors

The palette pairs a grounded cobalt primary (`#2458A5` in light, `#A9C7FF` in dark) with neutral cool surfaces (`#FAFBFF` in light, `#111318` in dark). Outgoing messages sit on `primary-container`, while incoming messages sit on `surface-variant`.

- **Primary:** Core interactive actions (FAB, primary buttons, active tabs, links).
- **Primary Container:** Outgoing message bubbles and active navigation pill indicators.
- **Surface & Surface Variant:** Flat list backgrounds, incoming message bubbles, and tonal dividers.
- **Error:** Failed message states, inline validation errors, and destructive actions (leave group, remove member).

```yaml
colors:
  light:
    primary: "#2458A5"
    on-primary: "#FFFFFF"
    primary-container: "#D9E7FF"
    on-primary-container: "#0D2F60"
    surface: "#FAFBFF"
    on-surface: "#191C20"
    surface-variant: "#E0E4EC"
    on-surface-variant: "#424750"
    surface-container-low: "#F0F3FA"
    surface-container: "#EAEFF7"
    surface-container-high: "#E2E8F2"
    outline: "#737782"
    outline-variant: "#C3C6D3"
    error: "#BA1A1A"
    on-error: "#FFFFFF"
    error-container: "#FFDAD6"
    on-error-container: "#410002"
    success: "#146C2E"
    on-success: "#FFFFFF"
    warning-container: "#FFEDE4"
    on-warning-container: "#4F2500"
  dark:
    primary: "#A9C7FF"
    on-primary: "#00315F"
    primary-container: "#17477F"
    on-primary-container: "#D9E7FF"
    surface: "#111318"
    on-surface: "#E2E2E9"
    surface-variant: "#424750"
    on-surface-variant: "#C2C6D0"
    surface-container-low: "#191C21"
    surface-container: "#1E2128"
    surface-container-high: "#282B33"
    outline: "#8D909C"
    outline-variant: "#434751"
    error: "#FFB4AB"
    on-error: "#690005"
    error-container: "#93000A"
    on-error-container: "#FFDAD6"
    success: "#6DD58C"
    on-success: "#003913"
    warning-container: "#4F2500"
    on-warning-container: "#FFDCC6"
```

## Typography

All text uses **Roboto** for native Android fidelity and crisp rendering of Vietnamese diacritics across all weights. Sizes follow the Material 3 type scale adapted to the messaging hierarchy (22px screen titles, 16px conversation names and message text, 14px previews and timestamps, 12px message status and badges).

```yaml
typography:
  title-lg:
    fontFamily: "Roboto"
    fontSize: 22px
    fontWeight: 500
    lineHeight: 28px
    letterSpacing: 0px
  title-md:
    fontFamily: "Roboto"
    fontSize: 16px
    fontWeight: 500
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: "Roboto"
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
    letterSpacing: 0.25px
  body-md:
    fontFamily: "Roboto"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
    letterSpacing: 0.25px
  body-sm:
    fontFamily: "Roboto"
    fontSize: 12px
    fontWeight: 400
    lineHeight: 16px
    letterSpacing: 0.4px
  label-lg:
    fontFamily: "Roboto"
    fontSize: 14px
    fontWeight: 500
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: "Roboto"
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
    letterSpacing: 0.5px
  label-sm:
    fontFamily: "Roboto"
    fontSize: 12px
    fontWeight: 400
    lineHeight: 16px
    letterSpacing: 0.4px
```

## Layout & Spacing

The layout uses a single-column **Fluid Grid** on mobile (`390px` viewport) with a strict `16px` horizontal page margin (`page-margin`). On tablet (`600dp+` / `768px+`), the inbox and active conversation adopt a two-pane list-detail split. Every spacing token aligns to the 4dp/8dp Material 3 grid (`4, 8, 12, 16, 24, 32`).

- **Touch Targets:** Every interactive control (icon buttons, list rows, attachment chips, retry actions) maintains a minimum `48×48px` hit area.
- **System Chrome:** Mobile screens include an Android status bar at the top and a gesture navigation bar pill at the bottom.
- **Message Bubbles:** Maximum width `80%` of the thread width; outgoing messages align right on `primary-container`, incoming align left on `surface-variant`. Group threads display the sender's name above each incoming message cluster.

```yaml
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  2xl: 32px
  page-margin: 16px
```

## Elevation & Depth

Hierarchy relies on **tonal surface steps** (`surface` → `surface-container-low` → `surface-container`) and subtle `outline-variant` hairlines rather than drop shadows. Floating Action Buttons and bottom sheets may use a minimal, crisp Material 3 tonal surface with no heavy shadow blur. Nested cards and glassmorphic blurs are strictly avoided.

## Shapes

Rounded corners soften interactive containers while keeping message threads structured:
- Message bubbles use `16px` (`lg`), with a subtle `4px` (`xs`) tail corner on consecutive cluster edges if desired.
- Input fields and media attachments use `12px` (`md`).
- Buttons, navigation pills, badges, and avatars use `full` (`9999px`).

```yaml
rounded:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  full: 9999px
```

## Motion & Micro-interaction

Motion is restrained, responsive, and purposeful—never decorative.

```yaml
motion:
  state-change:
    duration: 120ms
    delay: 0ms
    timingFunction: cubic-bezier(0.2, 0, 0, 1)
  surface-enter:
    duration: 220ms
    delay: 0ms
    timingFunction: cubic-bezier(0.05, 0.7, 0.1, 1)
  surface-exit:
    duration: 150ms
    delay: 0ms
    timingFunction: cubic-bezier(0.3, 0, 0.8, 0.15)
```

## Voice & Content Strategy

- **Language:** Natural, polite, concise Vietnamese with accurate diacritics throughout (`Đăng nhập`, `Tin nhắn`, `Danh bạ`, `Cá nhân`, `Đang gửi`, `Đã gửi`, `Gửi thất bại`, `Thử lại`, `Mở bằng Bản đồ`).
- **Tone:** Calm, clear, practical—no slang or marketing fluff.
- **Capitalisation:** Sentence case for all headings, tabs, buttons, and labels.
- **Relative Time:** Compact Vietnamese timestamps (`Vừa xong`, `5 phút`, `14:32`, `Hôm qua`, `24/11`).
- **Offline & Error Copy:** State the condition and the next action clearly (`Không có kết nối mạng. Đang hiển thị tin nhắn đã lưu.`, `Không thể gửi tin nhắn · Thử lại`).

## Components

Component implementations live as individual HTML sticker sheet files in
`components/`. Each file contains the canonical, styled HTML components with
`<!-- COMPONENT: name -->` comment markers. Read each file before use.

- **Avatar** → `@components/avatar.html` — `avatar-image`, `avatar-editable`
- **Badge** → `@components/badge.html` — `badge-numeric`, `badge-dot`
- **Button** → `@components/button.html` — `button-primary`, `button-icon`, `button-with-icon`
- **Input Field** → `@components/input-field.html` — `input-field-text`, `input-field-icon`
- **List** → `@components/list.html` — `list-single`, `list-two-line`, `list-leading-icon`, `list-trailing-action`, `list-interactive`, `list-interactive-2`
- **Tabs** → `@components/tabs.html` — `tabs-horizontal`, `tabs-with-icon`
