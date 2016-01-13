/**
 * @author julkar nain 
 */
package org.opensrp.register.mcare.service.scheduling;

import static java.text.MessageFormat.format;
import static org.opensrp.dto.BeneficiaryType.elco;
import static org.opensrp.register.mcare.OpenSRPScheduleConstants.ELCOSchedulesConstants.ELCO_SCHEDULE_PSRF;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.opensrp.common.AllConstants.ELCOSchedulesConstantsImediate;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.repository.AllActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ELCOScheduleService {
	
	private static Logger logger = LoggerFactory.getLogger(ELCOScheduleService.class.toString());
	private final ScheduleTrackingService scheduleTrackingService;
	private HealthSchedulerService scheduler;
	private AllActions allActions;
	
	@Autowired
	public ELCOScheduleService(HealthSchedulerService scheduler,ScheduleTrackingService scheduleTrackingService,AllActions allActions)
	{
		this.scheduler = scheduler;
		this.scheduleTrackingService = scheduleTrackingService;
		this.allActions = allActions;
	}
	
	public void enrollIntoMilestoneOfPSRF(String caseId, String date)
	{
	    logger.info(format("Enrolling Elco into PSRF schedule. Id: {0}", caseId));
	    
		scheduler.enrollIntoSchedule(caseId, ELCO_SCHEDULE_PSRF, date);
	}
	private  Date getDateTime(){		
		InputStream input = ELCOScheduleService.class.getClassLoader().getResourceAsStream("imdediate-elco-psrf.json");
		String result = "";
	    try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(input));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        result = sb.toString();
	        
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    Date todayDate = new Date();
	    try {
			JSONObject jsonObj = new JSONObject(result);			
			JSONArray milestones = jsonObj.getJSONArray("milestones");
			for (int k = 0; k < milestones.length(); k++) {
				JSONObject jsonObjs = new JSONObject(milestones.getJSONObject(k).getString("scheduleWindows").toString());
				String max = jsonObjs.getString("due").replace("[", "");
				max = max.replace("]", "").replace("Weeks", "").replaceAll("\"", "").trim();
				int weeks = Integer.parseInt(max);
				Date today = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(today);
				calendar.add(Calendar.WEEK_OF_YEAR,weeks);
				todayDate = calendar.getTime();				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
		return todayDate;
	}
	public void imediateEnrollIntoMilestoneOfPSRF(String caseId, String date,String provider)
	{
	    logger.info(format("Enrolling Elco into PSRF schedule. Id: {0}", caseId));	  
	    scheduler.enrollIntoSchedule(caseId, ELCOSchedulesConstantsImediate.IMD_ELCO_SCHEDULE_PSRF, date);	 
	    /*
	    logger.info("Schedule Tracking Service get query result : "+scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId)).get(0).getExternalId());
	    String caseID = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId)).get(0).getExternalId();
	    String scheduleName = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId)).get(0).getScheduleName();
	    String visitCode = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId)).get(0).getScheduleName();
	    logger.info("visitCode : "+visitCode);*/
	    DateTime expiryDate = new DateTime(getDateTime());
	    DateTime startDate = new DateTime();	
	    allActions.addOrUpdateAlert(new Action(caseId, provider, ActionData.createAlert(elco, ELCO_SCHEDULE_PSRF, ELCO_SCHEDULE_PSRF, AlertStatus.upcoming, startDate, expiryDate)));
	   
	}
	
}
