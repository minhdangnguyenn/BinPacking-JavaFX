# Optimization Algorithms – WS2526

**Author:** Dang Minh Nguyen  
**Date:** April 2026

---

## 1. Programming Exercise

### Placement Strategy: Bottom–Left
The algorithm places rectangles using the classical **Bottom–Left heuristic**.

---

### Ordering Strategy (Greedy)

- **Area Descending**
    - If two rectangles have equal area, the order is refined by **descending side length**.

---

### Neighborhood (Local Search)

- **Geometry**
    - Unpack half of the boxes from the current solution and repack them.

- **Permutation**
    - Swap two random rectangles in the list.
    - Reverse a random subsequence of rectangles.

- **Overlap**
    - Resolve all overlaps.
    - Repack the most overlapping box.
    - Repack the least-used box.
    - Repack the most-used box.
    - Move the largest rectangle out of the most-overlapping box.

---

## 2. Tuning the Algorithms and Neighborhoods

The original algorithms from the lecture are slow when the **entire neighborhood** is evaluated in every iteration. Two tuning techniques were applied.

### Early Stopping
- Local search stops if **no improvement is found for 10 consecutive iterations**.
- The counter resets whenever a better solution is found.
- This prevents wasted computation in stagnating regions.

### Reducing the Neighborhood
Instead of evaluating all neighbors, only a heuristically selected subset is explored:
- Boxes with overlaps
- Boxes with high occupancy
- A small number of random diverse candidates

---

## 3. Results – Full Comparison of All Methods

All strategies are compared across:
- **Greedy:** Area Descending, Side Length Descending
- **Local Search:** Geometry, Overlap, Permutation
- **Instance Sizes:** Small, Large

---

## 4. Mean Boxes Used

### Greedy – Area Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_area_desc_small/chart1_mean_boxes.png) | ![](src/test/visual/greedy/greedy_area_desc_large/chart1_mean_boxes.png) |

### Greedy – Side Length Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_side_desc_small/chart1_mean_boxes.png) | ![](src/test/visual/greedy/greedy_side_desc_large/chart1_mean_boxes.png) |

### Local Search – Geometry

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_geometry_small/chart1_mean_boxes.png) | ![](src/test/visual/ls/ls_geometry_large/chart1_mean_boxes.png) |

### Local Search – Overlap

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_overlap_small/chart1_mean_boxes.png) | ![](src/test/visual/ls/ls_overlap_large/chart1_mean_boxes.png) |

### Local Search – Permutation

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_permutation_small/chart1_mean_boxes.png) | ![](src/test/visual/ls/ls_permutation_large/chart1_mean_boxes.png) |

---

## 5. Min / Mean / Max Boxes

### Greedy – Area Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_area_desc_small/chart2_min_mean_max_boxes.png) | ![](src/test/visual/greedy/greedy_area_desc_large/chart2_min_mean_max_boxes.png) |

### Greedy – Side Length Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_side_desc_small/chart2_min_mean_max_boxes.png) | ![](src/test/visual/greedy/greedy_side_desc_large/chart2_min_mean_max_boxes.png) |

### Local Search – Geometry

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_geometry_small/chart2_min_mean_max_boxes.png) | ![](src/test/visual/ls/ls_geometry_large/chart2_min_mean_max_boxes.png) |

### Local Search – Overlap

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_overlap_small/chart2_min_mean_max_boxes.png) | ![](src/test/visual/ls/ls_overlap_large/chart2_min_mean_max_boxes.png) |

### Local Search – Permutation

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_permutation_small/chart2_min_mean_max_boxes.png) | ![](src/test/visual/ls/ls_permutation_large/chart2_min_mean_max_boxes.png) |

---

## 6. CPU vs Wall Time

### Greedy – Area Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_area_desc_small/chart3_cpu_vs_wall_time.png) | ![](src/test/visual/greedy/greedy_area_desc_large/chart3_cpu_vs_wall_time.png) |

### Greedy – Side Length Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_side_desc_small/chart3_cpu_vs_wall_time.png) | ![](src/test/visual/greedy/greedy_side_desc_large/chart3_cpu_vs_wall_time.png) |

### Local Search – Geometry

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_geometry_small/chart3_cpu_vs_wall_time.png) | ![](src/test/visual/ls/ls_geometry_large/chart3_cpu_vs_wall_time.png) |

### Local Search – Overlap

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_overlap_small/chart3_cpu_vs_wall_time.png) | ![](src/test/visual/ls/ls_overlap_large/chart3_cpu_vs_wall_time.png) |

### Local Search – Permutation

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_permutation_small/chart3_cpu_vs_wall_time.png) | ![](src/test/visual/ls/ls_permutation_large/chart3_cpu_vs_wall_time.png) |

---

## 7. Boxes Per Instance

### Greedy – Area Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_area_desc_small/chart4_boxes_per_instance.png) | ![](src/test/visual/greedy/greedy_area_desc_large/chart4_boxes_per_instance.png) |

### Greedy – Side Length Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_side_desc_small/chart4_boxes_per_instance.png) | ![](src/test/visual/greedy/greedy_side_desc_large/chart4_boxes_per_instance.png) |

### Local Search – Geometry

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_geometry_small/chart4_boxes_per_instance.png) | ![](src/test/visual/ls/ls_geometry_large/chart4_boxes_per_instance.png) |

### Local Search – Overlap

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_overlap_small/chart4_boxes_per_instance.png) | ![](src/test/visual/ls/ls_overlap_large/chart4_boxes_per_instance.png) |

### Local Search – Permutation

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_permutation_small/chart4_boxes_per_instance.png) | ![](src/test/visual/ls/ls_permutation_large/chart4_boxes_per_instance.png) |

---

## 8. Scaling Behaviour

### Greedy – Area Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_area_desc_small/chart5_scaling.png) | ![](src/tets/visual/greedy/greedy_area_desc_large/chart5_scaling.png) |

### Greedy – Side Length Descending

| Small | Large |
|-------|-------|
| ![](src/test/visual/greedy/greedy_side_desc_small/chart5_scaling.png) | ![](src/test/visual/greedy/greedy_side_desc_large/chart5_scaling.png) |

### Local Search – Geometry

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_geometry_small/chart5_scaling.png) | ![](src/test/visual/ls/ls_geometry_large/chart5_scaling.png) |

### Local Search – Overlap

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_overlap_small/chart5_scaling.png) | ![](src/test/visual/ls/ls_overlap_large/chart5_scaling.png) |

### Local Search – Permutation

| Small | Large |
|-------|-------|
| ![](src/test/visual/ls/ls_permutation_small/chart5_scaling.png) | ![](src/test/visual/ls/ls_permutation_large/chart5_scaling.png) |
