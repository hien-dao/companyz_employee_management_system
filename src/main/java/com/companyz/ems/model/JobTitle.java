package com.companyz.ems.model;

/**
 * Represents a job title/role within the organisation.
 * <p>
 * This simple model holds an identifier, a human-friendly title name
 * and an optional description. It is used by organizational tables
 * and assignment/history records.
 * </p>
 */
public class JobTitle {
    /** Primary key identifier for the job title. */
    private int jobTitleId;

    /** Human-friendly name of the job title (e.g. "Software Engineer"). */
    private String titleName;

    /** Optional longer description of the job title. */
    private String description;

    /**
     * Returns the numeric identifier for this job title.
     *
     * @return job title id
     */
    public int getJobTitleId() {
        return jobTitleId;
    }

    /**
     * Sets the numeric identifier for this job title.
     *
     * @param jobTitleId id to set
     */
    public void setJobTitleId(int jobTitleId) {
        this.jobTitleId = jobTitleId;
    }

    /**
     * Returns the job title name.
     *
     * @return title name, or {@code null} if not set
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * Sets the job title name.
     *
     * @param titleName human friendly title
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * Returns the job title description.
     *
     * @return description, or {@code null} if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the job title description.
     *
     * @param description longer description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
