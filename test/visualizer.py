import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
import pandas as pd
import numpy as np

# ── Load Data ─────────────────────────────────────────────────────────────────
file = "csv/localsearch_overlap_large.csv"
df = pd.read_csv(file)

# Aggregated stats per group
stats = df.groupby('NumRects').agg(
    n=('NumBoxes', 'count'),
    mean_boxes=('NumBoxes', 'mean'),
    std_boxes=('NumBoxes', 'std'),
    min_boxes=('NumBoxes', 'min'),
    max_boxes=('NumBoxes', 'max'),
    mean_cpu=('CpuTime_ms', 'mean'),
    std_cpu=('CpuTime_ms', 'std'),
    mean_wall=('WallTime_ms', 'mean'),
    std_wall=('WallTime_ms', 'std'),
).reset_index().sort_values('NumRects')

groups = [f"{r} rects" for r in stats['NumRects']]
x      = np.arange(len(groups))

# ── Style ─────────────────────────────────────────────────────────────────────
plt.style.use('seaborn-v0_8-whitegrid')
plt.rcParams.update({
    'figure.dpi': 150,
    'axes.titlesize': 13,
    'axes.titleweight': 'bold',
    'axes.labelsize': 11,
    'xtick.labelsize': 10,
    'ytick.labelsize': 10,
    'legend.fontsize': 10,
})

BLUE   = '#4C72B0'
ORANGE = '#DD8452'
GREEN  = '#55A868'
RED    = '#C44E52'

# ── Chart 1: Mean Boxes ± Std Dev ─────────────────────────────────────────────
fig, ax = plt.subplots(figsize=(9, 5))
bars = ax.bar(x, stats['mean_boxes'], yerr=stats['std_boxes'],
              color=BLUE, capsize=6, width=0.5, edgecolor='white',
              error_kw=dict(elinewidth=1.5, ecolor='#333333'))
ax.set_xticks(x)
ax.set_xticklabels(groups)
ax.set_xlabel('Problem Size (Rectangles)')
ax.set_ylabel('Mean Boxes Used')
ax.set_title(f'{file}\n(error bars = ±1 std dev)')
for bar, val, std in zip(bars, stats['mean_boxes'], stats['std_boxes']):
    ax.text(bar.get_x() + bar.get_width() / 2, bar.get_height() + std + 1,
            f'{val:.1f}', ha='center', va='bottom', fontsize=10, fontweight='bold')
ax.set_ylim(0, stats['max_boxes'].max() * 1.2)
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
plt.tight_layout()
plt.savefig('chart1_mean_boxes.png', dpi=150, bbox_inches='tight')
plt.close()
print("Saved chart1_mean_boxes.png")

# ── Chart 2: Min / Mean / Max Boxes ──────────────────────────────────────────
fig, ax = plt.subplots(figsize=(9, 5))
w = 0.25
ax.bar(x - w, stats['min_boxes'],  width=w, color=GREEN,  label='Min Boxes',  edgecolor='white')
ax.bar(x,     stats['mean_boxes'], width=w, color=BLUE,   label='Mean Boxes', edgecolor='white')
ax.bar(x + w, stats['max_boxes'],  width=w, color=RED,    label='Max Boxes',  edgecolor='white')
ax.set_xticks(x)
ax.set_xticklabels(groups)
ax.set_xlabel('Problem Size (Rectangles)')
ax.set_ylabel('Number of Boxes')
ax.set_title(f'{file} — Min / Mean / Max Boxes per Problem Size')
ax.legend()
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
plt.tight_layout()
plt.savefig('chart2_min_mean_max_boxes.png', dpi=150, bbox_inches='tight')
plt.close()
print("Saved chart2_min_mean_max_boxes.png")

# ── Chart 3: CPU vs Wall Time Scaling ─────────────────────────────────────────
fig, ax = plt.subplots(figsize=(9, 5))
ax.plot(stats['NumRects'], stats['mean_cpu'],  marker='o', color=BLUE,   linewidth=2, label='CPU Time (ms)')
ax.plot(stats['NumRects'], stats['mean_wall'], marker='s', color=ORANGE, linewidth=2, linestyle='--', label='Wall Time (ms)')
ax.fill_between(stats['NumRects'],
                stats['mean_cpu']  - stats['std_cpu'],
                stats['mean_cpu']  + stats['std_cpu'],  alpha=0.15, color=BLUE)
ax.fill_between(stats['NumRects'],
                stats['mean_wall'] - stats['std_wall'],
                stats['mean_wall'] + stats['std_wall'], alpha=0.15, color=ORANGE)
for _, row in stats.iterrows():
    ax.annotate(f"{row['mean_cpu']:.0f}",  (row['NumRects'], row['mean_cpu']),
                textcoords='offset points', xytext=(5,  5),  fontsize=9, color=BLUE)
    ax.annotate(f"{row['mean_wall']:.0f}", (row['NumRects'], row['mean_wall']),
                textcoords='offset points', xytext=(5, -14), fontsize=9, color=ORANGE)
ax.set_xlabel('Number of Rectangles')
ax.set_ylabel('Time (ms)')
ax.set_title(f'{file}\n(shaded = ±1 std dev)')
ax.legend()
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
plt.tight_layout()
plt.savefig('chart3_cpu_vs_wall_time.png', dpi=150, bbox_inches='tight')
plt.close()
print("Saved chart3_cpu_vs_wall_time.png")

# ── Chart 4: Boxes per Instance (consistency scatter) ────────────────────────
fig, ax = plt.subplots(figsize=(10, 5))
colors_map = {1000: BLUE, 3000: ORANGE, 5000: GREEN, 10000: RED}
for nr, grp in df.groupby('NumRects'):
    ax.scatter(grp['Instance'], grp['NumBoxes'], label=f'{nr} rects',
               color=colors_map[nr], s=60, zorder=3)
    ax.plot(grp['Instance'], grp['NumBoxes'], color=colors_map[nr], alpha=0.4, linewidth=1)
ax.set_xlabel('Instance Number')
ax.set_ylabel('Number of Boxes')
ax.set_title(f'{file}\n(consistency across runs)')
ax.legend(title='Problem Size')
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
plt.tight_layout()
plt.savefig('chart4_boxes_per_instance.png', dpi=150, bbox_inches='tight')
plt.close()
print("Saved chart4_boxes_per_instance.png")

# ── Chart 5: Linear Scaling Fit ───────────────────────────────────────────────
fig, ax = plt.subplots(figsize=(9, 5))
ax.plot(stats['NumRects'], stats['mean_boxes'], marker='o', color=BLUE, linewidth=2, label='Mean Boxes')
coeffs = np.polyfit(stats['NumRects'], stats['mean_boxes'], 1)
fit_y  = np.polyval(coeffs, stats['NumRects'])
ax.plot(stats['NumRects'], fit_y, '--', color='gray', linewidth=1.5,
        label=f'Linear fit: y = {coeffs[0]:.4f}x + {coeffs[1]:.2f}')
ax.set_xlabel('Number of Rectangles')
ax.set_ylabel('Mean Boxes Used')
ax.set_title(f'{file} — Boxes Scale Linearly with Rectangles')
ax.legend()
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
plt.tight_layout()
plt.savefig('chart5_scaling.png', dpi=150, bbox_inches='tight')
plt.close()
print("Saved chart5_scaling.png")

print("\nDone! All 5 charts saved.")