library ANCConfig

/*
  Description: The proportion of women in the adult population
    (18 years or older) with a BMI less than 18.5s
*/


define "Population prevalence of undernourishment":
  21 '%'

/*
  Description: The proportion of pregnant women in the population
    with anaemia (haemoglobin level less than 11 g/dl)
*/


define "Population prevalence of anaemia":
  41 '%'

/*
  Description: Women in the population are likely to have low dietary
    calcium intake (less than 900 mg of calcium per day)
*/


define "Population with low dietary calcium intake":
  true

/*
Description: The tuberculosis prevalence in the general population
  in number of cases per 100 000 persons or greater
*/


define "Population prevalence of tuberculosis (TB)":
  102

/*
  Description: Vitamin A deficiency is a severe public health problem
    if 5% or more of women in a population have a history of night
    blindness in their most recent pregnancy in the previous 3–5 years
    that ended in a live birth, or if 20% or more of pregnant women
    have a serum retinol level below 0.70 µmol/L
*/


define "Population with vitamin A deficiency":
  true

/*
  Description: The percentage of individuals in the general population
    infected with at least one species of soil-transmitted helminth
*/


define "Population prevalence of soil-transmitted helminth infection":
  21 '%'

/*
  Description: HIV incidence number of cases per 100 person-years
*/


define "Population incidence of HIV in the absence of PrEP":
  System.Quantity { value: 4, unit: '{Person-years}' }

/*
  Description: HIV prevalence is consistently over 5% in at least one
    defined key population but is below 1% in pregnant women attending
    antenatal clinics
*/


define "Population prevalence of HIV in key populations (men who have sex with men, people in prison or other closed settings, people who inject drugs, sex workers and transgender people) ":
  true

/*
  Description: The proportion of pregnant women in the population who are
    HIV positive
*/


define "Population prevalence of HIV in pregnant women":
  6 '%'

/*
  Description: Whether the setting is a malaria-endemic setting
*/


define "Malaria-endemic setting":
  true

/*
  Description: The proportion of pregnant women in the population with syphilis
*/


define "Population prevalence of syphilis in pregnant women":
  6 '%'

/*
  Description: The proportion of hepatitis B seroprevalence in the
    general population
*/


define "Population prevalence of hepatitis B":
  3 '%'

/*
  Description: The proportion of hepatitis C virus (HCV) antibody
    seroprevalance in the general population
*/


define "Population prevalence of hepatitis C":
  3 '%'

/*
  Description: "WHO does not recommend universal screening for violence of
    women attending health care. WHO does encourage health-care providers
    to raise the topic with women who have injuries or conditions that they
    suspect may be related to violence. All of the following must be in
    place at the health facility for this to be TRUE:
      a. A protocol or standard operating procedure for intimate partner
        violence (IPV)
      b. A health worker trained on how to ask about IPV and how to provide
        the minimum response or beyond
      c. A private setting
      d. A way to ensure confidentiality
      e. Time to allow for appropriate disclosure AND
      f. A system for referral in place."
*/


define "Minimum requirements for IPV assessment":
  true

/*
  Description: Is an ultrasound machine available and functional at your
    facility and a trained health worker available to use it?
*/


define "Ultrasound available at the health-care facility":
  true

/*
  Description: The proportion of pregnant women in the health-care facility's
    catchment area who are HIV positive, based on the number of pregnant
    women attending antenatal clinics at the health-care facility
*/


define "Prevalence of HIV in pregnant women in the catchment area":
  2 '%'