import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.vgu.cs.engine.entity.dhis2.model.TrackedEntityInstance;
import com.vgu.cs.engine.entity.omop.MeasurementEntity;
import com.vgu.cs.engine.entity.omop.ObservationEntity;
import com.vgu.cs.engine.entity.omop.PersonEntity;
import com.vgu.cs.engine.entity.omop.SpecimenEntity;
import com.vgu.cs.ma.service.model.business.dhis2.TrackedEntityInstanceModel;
import com.vgu.cs.ma.service.model.business.fhir.ObservationFModel;
import com.vgu.cs.ma.service.model.business.fhir.PatientFModel;
import com.vgu.cs.ma.service.model.business.fhir.SpecimenFModel;
import com.vgu.cs.ma.service.model.business.omop.MeasurementOModel;
import com.vgu.cs.ma.service.model.business.omop.ObservationOModel;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.SpecimenOModel;
import com.vgu.cs.ma.service.model.data.dhis2.TrackedEntityDModel;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Specimen;

public class Logic {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        FhirContext context = FhirContext.forDstu3();
        IParser parser = context.newJsonParser().setPrettyPrint(true);

//        _person(parser);
//        _observation(parser);
//        _measurement(parser);
//        _specimen(parser);
        _tei(parser);

        System.exit(0);
    }

    private static void _person(IParser parser){
        PersonEntity person = new PersonEntity();
        person.person_id = 1105487;
        person.gender_concept_id = 8507;
        person.year_of_birth = 1983;
        person.month_of_birth = 5;
        person.day_of_birth = 1;
        person.birth_datetime = "1983-05-01 00:00:00";
        person.race_concept_id = 8527;
        person.ethnicity_concept_id = 38003564;
        person.location_id = 17911;
        person.provider_id = 685;
        person.care_site_id = 19001;
        person.person_source_value = "00013D2EFD8E45D1";
        person.gender_source_value = "1";
        person.gender_source_concept_id = -1;
        person.race_source_value = "1";
        person.race_source_concept_id = -1;
        person.ethnicity_source_value = "1";
        person.ethnicity_source_concept_id = -1;

//        System.out.println(GSON.toJson(person));

//        System.out.println(parser.encodeResourceToString(PatientFModel.INSTANCE.constructFhir(person)));
//        System.out.println(GSON.toJson(PersonOModel.INSTANCE.constructOmop(PatientFModel.INSTANCE.constructFhir(person))));
    }

    private static void _observation(IParser parser){
        ObservationEntity obs = new ObservationEntity();
        obs.observation_id = 2997584;
        obs.person_id = 1100243548;
        obs.observation_concept_id = 2614621;
        obs.observation_date = "2019-03-12";
        obs.observation_datetime = "2019-03-12 00:00:00";
        obs.observation_type_concept_id = 45594739;
        obs.value_as_number = 791;
        obs.value_as_string = "791";
        obs.value_as_concept_id = 2617225;
        obs.qualifier_concept_id = 3010;
        obs.unit_concept_id = 45756938;
        obs.provider_id = 68886;
        obs.visit_occurrence_id = 90676;
        obs.observation_source_value = "V4502";
        obs.observation_source_concept_id = 18572;
        obs.unit_source_value = "FB11";
        obs.qualifier_source_value = "VMMO-8";

        System.out.println(GSON.toJson(obs));

        System.out.println(parser.encodeResourceToString(ObservationFModel.INSTANCE.constructFhir(obs)));
        System.out.println(GSON.toJson(ObservationOModel.INSTANCE.constructOmop(ObservationFModel.INSTANCE.constructFhir(obs))));
    }

    private static void _measurement(IParser parser){
        MeasurementEntity msm = new MeasurementEntity();
        msm.measurement_id = 70098014;
        msm.person_id = 322045199;
        msm.measurement_concept_id = 2212731;
        msm.measurement_type_concept_id = 40660437;
        msm.measurement_date = "2019-09-04";
        msm.measurement_datetime = "2019-09-04 16:05:00";
        msm.operator_concept_id = 5907;
        msm.value_as_number = 5.664;
        msm.value_as_concept_id = 2617225;
        msm.unit_concept_id = 45756938;
        msm.range_low = 2.25;
        msm.range_high = 12.25;
        msm.provider_id = 977905;
        msm.visit_occurrence_id = 3937;
        msm.measurement_source_value = "85610";
        msm.measurement_source_concept_id = 2212731;
        msm.unit_source_value = "FB11";
        msm.value_source_value = "00-01";

        System.out.println(GSON.toJson(msm));

        Observation obs = ObservationFModel.INSTANCE.constructFhir(msm);
        System.out.println(parser.encodeResourceToString(obs));
        System.out.println(GSON.toJson(MeasurementOModel.INSTANCE.constructOmop(obs)));
    }

    private static void _specimen(IParser parser){
        SpecimenEntity spe = new SpecimenEntity();
        spe.specimen_id = 597010100;
        spe.person_id = 9009;
        spe.specimen_concept_id = 44818705;
        spe.specimen_type_concept_id = 2615107;
        spe.specimen_date = "2020-12-09";
        spe.specimen_datetime = "2020-12-09 12:45:00";
        spe.quantity = 299;
        spe.unit_concept_id = 2615107;
        spe.anatomic_site_concept_id = 4079044;
        spe.disease_status_concept_id = 4069590;
        spe.specimen_source_value = "009-01";
        spe.unit_source_value = "GMO1011";
        spe.anatomic_site_source_value = "SITE-A";
        spe.disease_status_source_value = "ON";

        System.out.println(GSON.toJson(spe));

        Specimen spec = SpecimenFModel.INSTANCE.constructFhir(spe);
        System.out.println(parser.encodeResourceToString(spec));
        System.out.println(GSON.toJson(SpecimenOModel.INSTANCE.constructOmop(spec)));
    }

    private static void _tei(IParser parser){
        TrackedEntityInstance tei = new TrackedEntityInstance();
        tei.setUniqueIdAttribute("19978784");
        tei.setTrackedEntityInstance("f0tQtc2X7Pw");
        tei.setZipCodeAttribute("160165");
        tei.setCityAttribute("Arlington");
        tei.setGenderAttribute("MALE");
        tei.setLastNameAttribute("Robinson");
        tei.setFirstNameAttribute("Patrick");
        tei.setBirthDateAttribute("1981-03-25");

        Patient patient = PatientFModel.INSTANCE.constructFhir(tei);
        System.out.println(parser.encodeResourceToString(patient));
        System.out.println(GSON.toJson(PersonOModel.INSTANCE.constructOmop(patient)));
    }
}
