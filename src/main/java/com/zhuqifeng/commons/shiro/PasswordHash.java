package com.zhuqifeng.commons.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class PasswordHash implements InitializingBean {
	private String algorithmName;
	private int hashIterations;

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public int getHashIterations() {
		return hashIterations;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasLength(algorithmName, "algorithmName mast be MD5、SHA-1、SHA-256、SHA-384、SHA-512");
	}

	public String toHex(Object source, Object salt) {
		return new SimpleHash(algorithmName, source, salt, hashIterations).toHex();
	}

}
