package projekt;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class Miniprojekt {
	public static void main(){
		 // connecting to a DSTU3 compliant server
	    FhirContext ctx = FhirContext.forDstu3();
	    
	    String serverBase = "http://funke.imi.uni-luebeck.de/public/base/";
	    //String serverBase = "http://fhirtest.uhn.ca/baseDstu3";
	     
	    IGenericClient client = ctx.newRestfulGenericClient(serverBase);
	}
	
	public static void createPatient (){
		// Create a patient object
        Patient patient = new Patient();
        // random number for the ID
        int patID = (int)(Math.random()*10000);
        //add an ID
        patient.addIdentifier()
           .setSystem("http://www.kh-hh.de/mio/patients")
           .setValue(Integer.toString((int)patID)); 
        // add patients gender
        patient.setGender(AdministrativeGender.MALE);
        //add patients name
        patient.addName()
        	.setUse(HumanName.NameUse.OFFICIAL)
        	.setFamily("Mythenmetz")
        	.addGiven("Hildegunst")
        	.addPrefix("von");
        // add patients BirthDate
        patient.setBirthDate(new GregorianCalendar(1498,7,6).getTime());
        //add address
        patient.addAddress().setCity("Lindwurmfeste").setCountry("Zamonien").setPostalCode("94041").addLine("4 Silbendrechslerstr.");
        //  add patients marital status to married  MaritalStatusCodes.M     
        CodeableConcept matStat = new CodeableConcept();
        matStat.addCoding().
        	setCode("D")
        	.setSystem("http://hl7.org/fhir/v3/MaritalStatus")
        	.setDisplay("Divorced");
       
        patient.setMaritalStatus(matStat);
        
        // Textuelle Zusammenfassung
        Narrative na = new Narrative();
        na.setDivAsString("Patient Hildegunst von Mythenmetz, geboren am 6.7.1489, Familienstand: geschieden, Adresse: Silbendrechslerstr. 4, 94041 Lindwurmfeste, Zamonien");
        na.setStatus(NarrativeStatus.GENERATED);
        patient.setText(na);
	}
	
	public static void createEncounter(Patient patient){

    	Encounter aufnahme = new Encounter();
    	int idNr = (int)(Math.random()*10000);
        aufnahme.addIdentifier()
            .setSystem("http://www.kh-hh.de/mio/Encounter")
            .setValue(Integer.toString((int)idNr));
    	aufnahme.setStatus(EncounterStatus.ARRIVED);
    	aufnahme.setClass_(new Coding().setCode("EMER").setSystem("http://hl7.org/fhir/v3/ActCode"));
    	aufnahme.setSubjectTarget(patient);
    	aufnahme.setPeriod(new Period().setStart(new GregorianCalendar(2017,9,1).getTime()));
    	aufnahme.setServiceProviderTarget();
    	aufenthaltKind.addReason().addCoding().setCode("3950001").setSystem("http://snomed.info/sct").setDisplay("Birth");
    	aufnahme.addParticipant().addType(new CodeableConcept()
				.addCoding(new Coding()
				.setCode("ADM")
				.setDisplay("admitter")
				.setSystem("http://hl7.org/fhir/v3/ParticipationType"))).setIndividual(value);
    	
		
	}
	
	
}


