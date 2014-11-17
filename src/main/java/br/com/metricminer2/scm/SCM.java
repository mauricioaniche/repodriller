package br.com.metricminer2.scm;

import java.util.List;

public interface SCM {

	List<ChangeSet> getChangeSets(String path);

	Commit detail(String id, String path);

}