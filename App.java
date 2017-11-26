package MIO.MIOFhir;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemContentModeEnumFactory;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataElement;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.hl7.fhir.dstu3.model.RelatedPerson;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        
     // connecting to a DSTU3 compliant server
        FhirContext ctx = FhirContext.forDstu3();
        
       // String serverBase = "http://funke.imi.uni-luebeck.de/public/base/";
        String serverBase = "http://fhirtest.uhn.ca/baseDstu3";
         
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
            
        // methods for the different exercises
        createPatientAndLaborWard(ctx, client);
        //getAllPatientsNames(client);
    }
    
    public static void createPatientAndLaborWard(FhirContext ctx,IGenericClient client )
    {
     // Create a patient object
        Patient patient = new Patient();
        // random number for the ID
        double patID = Math.random()*100;
       
        //add an ID
        patient.addIdentifier()
           .setSystem("http://www.kh-hh.de/mio/patients")
           .setValue(Double.toString((int)patID)); 
        // add patients gender
        patient.setGender(AdministrativeGender.FEMALE);
        
        //add patients name
        patient.addName()
        	.setUse(HumanName.NameUse.OFFICIAL)
        	.setFamily("Grünlich")
        	.addGiven("Antonie");
                
        // Add patients maiden name
        patient.addName()
        	.setUse(HumanName.NameUse.MAIDEN)
    		.setFamily("Buddenbrook")
    		.addGiven("Antonie");
        //Add patients nickname
        patient.addName()
        	.setUse(HumanName.NameUse.NICKNAME)
        	.addGiven("Tony");
        	
        // add patients BirthDate 1927-08-06
        patient.setBirthDate(new GregorianCalendar(1927,7,6).getTime());
        

        //  add patients marital status to married  MaritalStatusCodes.M     
        CodeableConcept matStat = new CodeableConcept();
        matStat.addCoding().
        	setCode("M")
        	.setSystem("http://hl7.org/fhir/v3/MaritalStatus")
        	.setDisplay("married");
       
        patient.setMaritalStatus(matStat);
        
        // Textuelle Zusammenfassung
        Narrative na = new Narrative();
        na.setDivAsString("Patient Antonie Grünlich, geborene Buddenbrook, Spitzname Tony, weibl., *6.8.1827, verheiratet");
        na.setStatus(NarrativeStatus.GENERATED);
        patient.setText(na);
        
        // export to json
        
        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        String encoded = jsonParser.encodeResourceToString(patient);
        System.out.println(encoded);
        
       
        
        // write json to a file
        Writer fw =null;
        try {
        	fw = new FileWriter("data/PatientGruenlich.txt"); 
        	fw.write(encoded);
        }catch(IOException e){
        	System.err.println("Fehler Beim Erstellen der Datei PatientGruenlich");
        }
        finally {
  		  if ( fw != null )
  		    try { fw.close(); } catch ( IOException e ) { e.printStackTrace(); }
        }
        
        //Erzeugen sie die Geburtsstation als Organisation für das "MIO" Krankenhaus Lübeck.
        Organization krankenhaus = new Organization();
    	krankenhaus.addType()
    		.addCoding()
    		.setCode("prov")
    		.setSystem("http://hl7.org/fhir/organization-type");
    	krankenhaus.setName("MIO-Krankenhaus");
    	krankenhaus.addAddress().setCity("Lübeck");
    	krankenhaus.setActive(true);
    	
    	//Textuelle Zusammenfassung
    	na = new Narrative();
    	na.setDivAsString("MIO- Krankenhaus in Lübeck");
    	na.setStatus(NarrativeStatus.GENERATED);
    	krankenhaus.setText(na);
    	
    	//create labor ward
    	Organization geburtsstation = new Organization();
    	geburtsstation.setName("Geburtsstation");
    	geburtsstation.setPartOfTarget(krankenhaus);
    	geburtsstation.addType().addCoding().setCode("dept").setSystem("http://hl7.org/fhir/organization-type");
    	
    	//Textuelle Zusammenfassung
    	na = new Narrative();
    	na.setDivAsString("Geburtsstation im MIO-Krankenhaus");
    	na.setStatus(NarrativeStatus.GENERATED);
    	geburtsstation.setText(na);
    	    	
    	//create doctor
    	Practitioner pracDoc = new Practitioner();
    	pracDoc.addName().addGiven("Mio").setFamily("Lundin").addPrefix("Dr.");
    	pracDoc.setGender(AdministrativeGender.MALE);
    	//set qualification doctor of med
    	CodeableConcept docQual = new CodeableConcept();
        docQual.addCoding().
        	setCode("MD")
        	.setSystem("http://hl7.org/fhir/v2/0360/2.7")
        	.setDisplay("Doctor of Medicine");
    	pracDoc.addQualification().setCode(docQual);
    	
    	na = new Narrative();
    	na.setDivAsString("Angestllter Dr. med. Mio Lundin, männlich" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	pracDoc.setText(na);
    	
    	PractitionerRole rolDoc = new PractitionerRole();
    	rolDoc.addCode().addCoding().setCode("doctor").setSystem("http://hl7.org/fhir/practitioner-role").setDisplay("Doctor");
    	rolDoc.setOrganizationTarget(geburtsstation);
    	rolDoc.setPractitionerTarget(pracDoc);
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Dr. med. Mio Lundin in der Rolle als Arzt in der Geburtsstation des MIO-Krankenhauses" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	rolDoc.setText(na);
    	
    	//create nurse
    	Practitioner pracNur = new Practitioner();
    	pracNur.addName().addGiven("Jum-Jum").setFamily("Svendson");
    	pracNur.setGender(AdministrativeGender.MALE);
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Angestellter Jum-Jum, männlich" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	pracNur.setText(na);
    	
        
    	PractitionerRole rolNur = new PractitionerRole();
    	rolNur.addCode().addCoding().setCode("nurse").setSystem("http://hl7.org/fhir/practitioner-role").setDisplay("Nurse");
    	rolNur.setOrganizationTarget(geburtsstation);
    	rolNur.setPractitionerTarget(pracNur);
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Jum-Jum Svendson in der Rolle als Krankenpfleger in der Geburtsstation des MIO-Krankenhauses" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	rolNur.setText(na);
    	
    	// Erzeugen sie einen geplanten Krankenhausaufenthalt für "Tony" zur Geburt 
    	// ihrer Tochter "Erika" für den 1.10.1846 auf der Geburtsstation im "MIO" Krankenhaus. 
    	 
    	Patient erika = new Patient();
    	erika.addName().addGiven("Erika").setFamily("Buddenbrook");
    	erika.setGender(AdministrativeGender.FEMALE);
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Patient Erika Buddenbrook, weibl." );
    	na.setStatus(NarrativeStatus.GENERATED);
    	erika.setText(na);
    	        
    	RelatedPerson relMom = new RelatedPerson();
    	relMom.setPatientTarget(erika);
    	relMom.setRelationship(new CodeableConcept()
    								.addCoding(new Coding()
    								.setCode("MTH")
    								.setDisplay("mother")
    								.setSystem("http://hl7.org/fhir/v3/RoleCode")));
    	relMom.addName().setFamily("Buddenbrook").setUse(NameUse.OFFICIAL).addGiven("Antonie");
    	relMom.addName().setUse(NameUse.NICKNAME).addGiven("Tony");
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Tony ist die Mutter von Erika" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	relMom.setText(na);
    	
    	Encounter aufenthalt = new Encounter();
    	aufenthalt.setStatus(EncounterStatus.PLANNED);
    	aufenthalt.setClass_(new Coding().setCode("IMP").setSystem("http://hl7.org/fhir/v3/ActCode"));
    	aufenthalt.setSubjectTarget(patient);  
    	aufenthalt.setPeriod(new Period().setStart(new GregorianCalendar(1846,9,1).getTime()));
    	aufenthalt.setServiceProviderTarget(geburtsstation);
    	aufenthalt.addReason().addCoding().setCode("386216000").setSystem("http://snomed.info/sct").setDisplay("Childbirth");
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Patient Tony Buddenbrook, geplante Aufnahme in der Geburtsstation am 1.10.1846 zur Entbindung" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	aufenthalt.setText(na);
    	
    	Encounter aufenthaltKind = new Encounter();
    	aufenthaltKind.setStatus(EncounterStatus.PLANNED);
    	aufenthaltKind.setClass_(new Coding().setCode("IMP").setSystem("http://hl7.org/fhir/v3/ActCode"));
    	aufenthaltKind.setSubjectTarget(erika);
    	aufenthaltKind.setPeriod(new Period().setStart(new GregorianCalendar(1846,9,1).getTime()));
    	aufenthaltKind.setServiceProviderTarget(geburtsstation);
    	aufenthaltKind.addReason().addCoding().setCode("3950001").setSystem("http://snomed.info/sct").setDisplay("Birth");
    	aufenthaltKind.setPartOfTarget(aufenthalt);
    	
    	//Textuelle Zusammmenfassung
    	na = new Narrative();
    	na.setDivAsString("Geplante Aufnahme von Erika Buddenbrook in der Geburtsstation am 1.10.1846 zur Geburt" );
    	na.setStatus(NarrativeStatus.GENERATED);
    	aufenthaltKind.setText(na);
    	
    	//Speichern sie die Station, das Krankenhaus, das Personal sowie den geplanten Aufenthalt
    	//xml+fhir Darstellung in einer Datei.
    	IParser xmlParser = ctx.newXmlParser();
    	xmlParser.setPrettyPrint(true);
    	xmlParser.setSuppressNarratives(false);
    	
    	Writer fw2 = null;
    	try{
    		fw = new FileWriter("data/Geburt.xml");
    		//Krankenhaus
    		fw.write(xmlParser.encodeResourceToString(krankenhaus));
    		fw.write(System.getProperty("line.separator"));
    		
    		//Geburtsstation
    		fw.write(xmlParser.encodeResourceToString(geburtsstation));
    		fw.write(System.getProperty("line.separator"));
    		
    		//Doctor
    		fw.write(xmlParser.encodeResourceToString(pracDoc));
    		fw.write(System.getProperty("line.separator"));
    		fw.write(xmlParser.encodeResourceToString(rolDoc));
    		fw.write(System.getProperty("line.separator"));
    	 		
    		//Nurse
    		fw.write(xmlParser.encodeResourceToString(pracNur));
    		fw.write(System.getProperty("line.separator"));
    		fw.write(xmlParser.encodeResourceToString(rolNur));
    		fw.write(System.getProperty("line.separator"));
    		
    		//Aufenthalt Mutter
    		fw.write(xmlParser.encodeResourceToString(aufenthalt));
    		fw.write(System.getProperty("line.separator"));
    		// Aufenthalt Kind
    		fw.write(xmlParser.encodeResourceToString(aufenthaltKind));
    		fw.write(System.getProperty("line.separator"));
    	}
    	catch (IOException e) {
			System.err.println("Fehler Beim Erstellen der Datei Geburt");
		}
    	finally {
			if(fw != null)
				try{fw.close();} catch (IOException e) {
					e.printStackTrace();
				}
		}
    }
    
    private static void getAllPatientsNames(IGenericClient client)
    {
    	
    	
    	//  Search for all patient
      Bundle results = client.search()
      		.forResource(Patient.class)
      		.returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
      		.elementsSubset("family")
      		.execute(); 
    	// Get patients names 
      List<String> patientsNames =new ArrayList<String>();
      for(BundleEntryComponent entry : results.getEntry()) {
    	  Patient p = (Patient)entry.getResource();
    	  patientsNames.add(p.getName().get(0).getFamily() +", " + p.getName().get(0).getGivenAsSingleString());
      }
      
      // write names to a file
      Writer fw =null;
      try {
      	fw = new FileWriter("data/PatientsNameList.txt"); 
      	for(String s:patientsNames){
      		fw.write(s + "\r\n");      		
      	}
      	
      }catch(IOException e){
      	System.err.println("Fehler Beim Erstellen der Datei PatientsNames");
      }
      finally {
		  if ( fw != null )
		    try { fw.close(); } catch ( IOException e ) { e.printStackTrace(); }
      }
           
    }
   
    }

