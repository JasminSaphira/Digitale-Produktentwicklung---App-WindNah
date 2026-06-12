# WindNah – Calculation Specification

Version: 1.0

Status: Draft

Related Documents:

* 01_PRD.md
* 04_ARCHITECTURE.md
* 06_API_SPEC.md

---

# 1. Purpose

This document defines all calculated metrics used within WindNah.

For each metric, the following are specified:

* purpose
* inputs
* formula
* assumptions
* sources
* transparency information
* test examples

All calculations must be implemented inside:

```text
core:domain
```

and exposed through dedicated Use Cases.

---

# 2. Calculation Principles

All calculations must:

* be deterministic
* be testable
* expose assumptions
* expose sources
* expose update frequency
* support transparency overlays

Calculated values must always be labeled as:

```text
Estimated Value
```

unless directly measured.

---

# 2.1 Transparency Overlay Standard

Every calculated metric must provide a transparency overlay.

The transparency overlay should be concise, understandable and readable within 10–20 seconds.

Each metric must provide the following information:

**What does this metric show?**

A short explanation of what the metric represents.

**How is it calculated?**

A one-sentence description of the calculation logic.

**Data Sources**

The primary sources used for the calculation.

**Updated**

The update frequency of the metric.

# 3. Current Output

## Purpose

Estimate the current electricity generation of a wind farm based on live wind conditions.

---

## UI Label

```text
Current Output
```

---

## Inputs

```text
Current Wind Speed (DWD)

Wind Turbine Rated Power (MaStR)

Hub Height (MaStR)

Turbine Status (MaStR)
```

---

## Assumptions

### Hellmann Exponent

```text
α = 0.14
```

Typical flat terrain in Northern Germany.

---

### Wake Effect

```text
85 %
```

Park efficiency

Represents approximately 15% wake losses.

---

### Cut-In Speed

```text
3 m/s
```

---

### Rated Wind Speed

```text
12 m/s
```

---

### Cut-Out Speed

```text
25 m/s
```

---

## Step 1

Calculate wind speed at hub height.

Formula:

```text
vHub = v10m × (hubHeight / 10)^α
```

---

## Step 2

Calculate turbine output.

If:

```text
vHub < 3 m/s
```

Output:

```text
0 %
```

---

If:

```text
3 ≤ vHub ≤ 12
```

Formula:

```text
Output = RatedPower × ((vHub − 3) / (12 − 3))³
```

---

If:

```text
12 < vHub ≤ 25
```

Output:

```text
100 %
```

of rated power.

---

If:

```text
vHub > 25
```

Output:

```text
0 %
```

---

## Step 3

Aggregate all active turbines.

Formula:

```text
Wind Farm Output
=
Sum(Turbine Outputs)
× 0.85
```

---

## Output Unit

```text
kW
```

or

```text
MW
```

---

## Transparency Overlay

What does this metric show?

This value estimates how much electricity the wind farm is currently generating.

How is it calculated?

The calculation combines live wind speed data with turbine specifications such as rated power and hub height.

Data Sources
Deutscher Wetterdienst (DWD)
Marktstammdatenregister (MaStR)

Updated

Every 15 minutes

---

# 4. Wind Farm Utilization

## Purpose

Show how much of the installed capacity is currently being used.

---

## Formula

```text
Current Output
/
Installed Capacity
× 100
```

---

## Unit

```text
%
```

---

# 5. Households Supplied

## Purpose

Translate electricity generation into an understandable real-world metric.

---

## UI Label

```text
Households Supplied
```

---

## Assumptions

Average German household consumption:

```text
3,500 kWh/year
```

Equivalent continuous power demand:

```text
0.4 kW
```

---

## Variant A

### Annual Households Supplied

Formula:

```text
Annual Production
/
3,500
```

---

## Variant B

### Currently Supplied Households

Formula:

```text
Current Output
/
0.4
```

---

## Output Unit

```text
Households
```

---

## Transparency Overlay
What does this metric show?

This value estimates how many average German households could be supplied with the generated electricity.

How is it calculated?

The wind farm's electricity production is compared with the average annual electricity consumption of a household.

Data Sources
Wind farm production estimates
Federal Statistical Office

Updated

Daily

---

# 6. CO₂ Savings

## Purpose

Show the environmental benefit of wind energy generation.

---

## UI Label

```text
CO₂ Savings
```

---

## Inputs

```text
Annual Production

Conventional Electricity Mix

Wind Energy Lifecycle Emissions
```

---

## Reference Values

### Conventional Electricity Mix

```text
363 g CO₂/kWh
```

Source:

Umweltbundesamt

---

### Wind Energy Lifecycle Emissions

```text
9 g CO₂/kWh
```

Source:

Lifecycle Assessment of Onshore Wind Energy

---

## Formula

```text
CO₂ Savings (t/year)

=
Annual Production
×
(363 − 9)
/
1,000,000
```

---

## Output Unit

```text
t CO₂/year
```

---

## Comparison A

### Driving Distance Equivalent

Assumption:

```text
150 g CO₂/km
```

Formula:

```text
CO₂ Savings
/
0.15
```

---

## Output

```text
Equivalent to X km driven by a passenger car.
```

---

## Comparison B

### Trees Equivalent

Assumption:

```text
12.5 kg CO₂/year
```

per beech tree.

Formula:

```text
CO₂ Savings (kg)
/
12.5
```

---

## Output

```text
Equivalent to the annual CO₂ absorption of X beech trees.
```

---

## Transparency Overlay

What does this metric show?

This value estimates how much CO₂ emissions are avoided each year through the electricity generated by this wind farm.

How is it calculated?

The annual electricity production is compared with the emissions of the conventional electricity mix.

Data Sources
Umweltbundesamt (UBA)
Wind farm production estimates
Updated

Daily
---

# 7. Local Energy Contribution

## Purpose

Show the local relevance of the selected wind farm.

---

## UI Label

```text
Local Energy Contribution
```

---

## Inputs

```text
Installed Capacity

Full Load Hours

Municipality Population
```

---

## Assumptions

### Full Load Hours

```text
1,900 h/year
```

---

### Electricity Consumption

```text
5,500 kWh
```

per resident and year.

---

## Step 1

Annual Production

Formula:

```text
Installed Capacity × 1,900
```

---

## Step 2

Municipal Consumption

Formula:

```text
Population × 5,500
```

---

## Step 3

Contribution

Formula:

```text
Annual Production
/
Municipal Consumption
× 100
```

---

## Output Unit

```text
%
```

---

## Transparency Overlay

What does this metric show?

This value estimates how much of the municipality's annual electricity demand could theoretically be covered by this wind farm.

How is it calculated?

The estimated annual production of the wind farm is compared with the estimated annual electricity consumption of the municipality.

Data Sources
Marktstammdatenregister (MaStR)
Municipality population data
Federal Statistical Office

Updated

Monthly

---

# 8. Municipal Revenue

## Purpose

Show the estimated financial contribution to the municipality.

---

## Inputs

```text
Annual Production
```

---

## Assumption

EEG §6:

```text
0.2 ct/kWh
```

---

## Formula

```text
Annual Production
×
0.002 €
```

---

## Output Unit

```text
€/year
```

---

## Transparency Overlay

What does this metric show?

This value estimates the annual financial contribution of the wind farm to the municipality.

How is it calculated?

The estimate is based on annual electricity production and the participation model defined in EEG §6.

Data Sources
Marktstammdatenregister (MaStR)
EEG §6

Updated

Monthly
---

# 9. Noise Estimation
## Purpose

Provide an understandable estimate of the perceived noise level around the wind farm.

## UI Label

Estimated Noise Level

## Inputs
- Distance to the wind farm
- Current wind speed (DWD)
- Current wind farm utilization
- Average rotor diameter
- Number of active turbines

## Assumptions
**Base Noise Level**

Estimated from average rotor diameter.

Rotor Diameter < 100 m

102 dB(A)

Rotor Diameter 100–130 m

105 dB(A)

Rotor Diameter > 130 m

108 dB(A)

**Wake and Environmental Effects**

Not considered.

The value is intended as an educational estimate and not as an official acoustic assessment.

## Calculation Logic
Estimate source noise based on rotor size.
Adjust source noise using current wind farm utilization.
Apply distance attenuation.
Aggregate active turbines.

## Output Unit

dB(A)

## Sound Comparisons

55 dB → Normal conversation

45 dB → Quiet residential street

35 dB → Library

## Transparency Overlay
**What does this metric show?**

This value estimates how loud the wind farm may be at the selected distance.

**How is it calculated?**

The estimate combines turbine size, current wind conditions, wind farm utilization and distance.

**Data Sources**
Deutscher Wetterdienst (DWD)
Marktstammdatenregister (MaStR)

**Updated**

Every 15 minutes

---

# 10. Wind Turbine Height Comparison

## Purpose

Provide understandable size comparisons.

---

## Inputs

```text
Hub Height

Rotor Diameter
```

---

## Formula

```text
Total Height
=
Hub Height
+
(Rotor Diameter / 2)
```

---

## Wind Farm Value

Formula:

```text
Average Total Height
=
Mean(Total Height)
```

---

## Comparison Objects

### Frauenkirche Dresden

```text
91 m
```

---

### Cologne Cathedral

```text
157 m
```

---

### Berlin TV Tower

```text
368 m
```

---

## Transparency Overlay

What does this metric show?

This comparison helps visualize the average size of the turbines in the selected wind farm.

How is it calculated?

The total turbine height is calculated using hub height and rotor radius and then averaged across all turbines.

Data Sources
Marktstammdatenregister (MaStR)

Updated

Whenever wind farm data is refreshed
---

# 11. Update Frequencies

| Metric                    | Frequency        |
| ------------------------- | ---------------- |
| Current Output            | Every 15 minutes |
| Wind Speed                | Every 15 minutes |
| Wind Direction            | Every 15 minutes |
| Households Supplied       | Daily            |
| CO₂ Savings               | Daily            |
| Local Energy Contribution | Monthly          |
| Municipal Revenue         | Monthly          |
| Noise Estimation          | Real-time        |
| Height Comparison         | Static           |

---

# 12. Unit Testing Requirements

Every calculation must provide tests for:

* minimum values
* average values
* maximum values
* invalid values
* missing values

Coverage target:

```text
> 90 %
```

for all calculation Use Cases.
