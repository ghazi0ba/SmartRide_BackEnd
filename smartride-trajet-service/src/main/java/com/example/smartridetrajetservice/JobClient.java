/*//TEST
package com.example.smartridetrajetservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name="job-s", url="http://localhost:8084")
public interface JobClient {
    @RequestMapping("jobs")
    public List<Job> getAllJobs();



}
*/