package esprit.jobms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private JobRepository jobRepository;

    private final JobProducer jobProducer;
    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    public JobService(JobRepository jobRepository, JobProducer jobProducer) {
        this.jobRepository = jobRepository;
        this.jobProducer = jobProducer;
    }
    /** Sauvegarde d'un Job dans la base de données et envoi d’un DTO à RabbitMQ **/

    //si save réussit mais l’envoi échoue, on aura un enregistrement sans message
    @Transactional
    public Job saveAndSendJob(Job jobEntity) {
        Job savedJob = jobRepository.save(jobEntity);
        log.info("Job sauvegardé en base : {}", savedJob.getService());
        // Construire un DTO à envoyer
        JobDTO jobDTO = new JobDTO(savedJob.getId(), savedJob.getService(), savedJob.isEtat());
        // Envoi asynchrone via RabbitMQ
        jobProducer.sendJob(jobDTO);
        return savedJob;

    }


    public Job addJob(Job job) {
        return jobRepository.save(job);
    }
    public Job getJobById(int id) {
        return jobRepository.findById(id).orElse(null);
    }
    public List<Job> getAll(){
        return jobRepository.findAll();
    }
    public Job updateJob(int id, Job newJob) {
        if (jobRepository.findById(id).isPresent()) {

           Job existingJob = jobRepository.findById(id).get();
            existingJob.setService(newJob.getService());
            existingJob.setEtat(newJob.isEtat());

            return jobRepository.save(existingJob);
        } else
            return null;
    }
    public String deleteJob(int id) {
        if (jobRepository.findById(id).isPresent()) {
            jobRepository.deleteById(id);
            return "Job supprimé";
        } else
            return "Job non supprimé";
    }
}
