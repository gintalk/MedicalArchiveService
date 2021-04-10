# MedicalArchiveService

## Table of contents
* [General info](#general-info)
  * [Responsibility](#responsibility)
  * [Technologies](#technologies)
* [Details](#details)
* [References](#references)

## General info
This is a part of a 3-layer back-end architecture:<br>
[API] <-> ***SERVICE*** <-> Database

### Responsibility
This server exposes API which serves HL7 FHIR-compliant medical data.

### Technologies
* [Apache Thrift] version: 0.14.1
* [HAPI FHIR] version: 5.3

## Details
Every common data model was designed with a different set of use cases 
in mind, resulting in little to no compatibility. This problem prompts 
the FHIR organization to come up with a [CDMs Harmonization 
Implementation Guide] on the harmonizing of FHIR and several 
popular common data models, which the bulk of the conversion logic in 
this project was based on. We have chosen the OHDSI OMOP common data 
model to be the format in which our data are stored in. Therefore, most 
of the work performed by this service involves converting data between 
OHDSI OMOP and HL7 FHIR.

## References
* OMOP Vocabularies: https://athena.ohdsi.org/vocabulary/list
* OHDSI OMOP CDM database setup guide: https://github.com/OHDSI/CommonDataModel/tree/v5.2.2/PostgreSQL
* Sample OMOP dataset: http://www.ltscomputingllc.com/downloads/
* HL7 FHIR documentation: https://www.hl7.org/fhir/documentation.html
* OMOP on FHIR: https://github.com/omoponfhir/omoponfhir-main-stu3.git

[API]: https://github.com/gintalk/MedicalArchiveAPI.git
[Apache Thrift]: https://thrift.apache.org/docs/
[HAPI FHIR]: https://hapifhir.io/hapi-fhir/docs/getting_started/introduction.html
[CDMs Harmonization Implementation Guide]: http://build.fhir.org/ig/HL7/cdmh/profiles.html