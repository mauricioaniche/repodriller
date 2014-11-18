package br.com.metricminer2.persistence;

public interface PersistenceMechanism {
	void write(Object... line);
	void close();
}
