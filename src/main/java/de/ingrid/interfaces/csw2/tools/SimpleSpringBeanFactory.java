/**
 * 
 */
package de.ingrid.interfaces.csw2.tools;

import de.ingrid.utils.tool.SpringUtil;

/**
 * @author joachim@wemove.com
 *
 */
public enum SimpleSpringBeanFactory {
	
	// Guaranteed to be the single instance  
	INSTANCE;
	
	SpringUtil util = null;
	String configFile = "spring/beans.xml";
	
	public <T> T getBean(String beanName, Class<T> clazz) {
		if (util == null) {
			util = new SpringUtil(configFile);
		}
		return util.getBean(beanName, clazz);
    }
	
	/**
     * Get a named bean. Throws a RuntimeException, if the bean does not exist
	 * @param <T>
	 * @param beanName
	 * @param clazz
	 * @return
	 */
	public <T> T getBeanMandatory(String beanName, Class<T> clazz) {
		if (util == null) {
			util = new SpringUtil("spring/beans.xml");
		}
		T bean = util.getBean(beanName, clazz);
		if (bean == null)
			throw new RuntimeException("Configuration error: Parameter '"+beanName+
					"' is missing in "+configFile);
		return bean;
    }
	
	public void setBeanConfig(String beanConfigFile) {
		this.configFile = beanConfigFile;
		util = new SpringUtil(beanConfigFile);
	}
	
}
