package bloodasp.galacticgreg.dynconfig;

import gregtech.api.GregTech_API;

import java.util.HashMap;
import java.util.Map;

import bloodasp.galacticgreg.GalacticGreg;
import bloodasp.galacticgreg.api.ModContainer;
import bloodasp.galacticgreg.api.ModDimensionDef;
import bloodasp.galacticgreg.registry.GalacticGregRegistry;

/**
 * This is the dynamic config class for every ore-vein that will generate config values according to the dimension and
 * mod name 
 */
public class DynamicOreMixWorldConfig {
	private String _mWorldGenName;
	private Map<String, Boolean> _mDynWorldConfigMap = null;
	private final String _mConfigName;
	
	private String getConfigKeyName(ModContainer pMC, ModDimensionDef pMDD)
	{
		return getConfigKeyName(pMC, pMDD, "");
	}
	
	private String getConfigKeyName(ModContainer pMC, ModDimensionDef pMDD, String pAdditionalName)
	{
		String tRet = String.format("%s_%s", pMC.getModName(), pMDD.getDimensionName());
		if (pAdditionalName.length() > 1)
			tRet = String.format("%s_%s", tRet, pAdditionalName);
		
		return tRet;
	}
	
	/**
	 * Init a new dynamic config for a given world-generator
	 * @param pWorldGenName
	 */
	public DynamicOreMixWorldConfig(String pWorldGenName)
	{
		_mWorldGenName = pWorldGenName;
		_mDynWorldConfigMap = new HashMap<String, Boolean>();
		_mConfigName = String.format("worldgen.%s", _mWorldGenName);
	}
	
	/**
	 * Check if this OreGen is enabled for a given Dimension, represented by pMDD
	 * @param pMDD The dimension in question
	 * @return true or false if *this* oregen is enabled in the worldgen config
	 */
	public boolean isEnabledInDim(ModDimensionDef pMDD)
	{
		String tDimIdentifier = pMDD.getDimIdentifier();
		if (_mDynWorldConfigMap.containsKey(tDimIdentifier))
			return _mDynWorldConfigMap.get(tDimIdentifier);
		else
			return false;
	}
	
	/**
	 * Initializes the dynamic oregen config.
	 * This must be called *AFTER* InitModContainers() has done its work
	 * @return true or false if the config init was successfull
	 */
	public boolean InitDynamicConfig()
	{
		try
		{
			for (ModContainer mc : GalacticGregRegistry.getModContainers())
			{
				if (!mc.getEnabled())
					continue;
				
				for (ModDimensionDef mdd : mc.getDimensionList())
				{
					String tDimIdentifier = mdd.getDimIdentifier();
					if (_mDynWorldConfigMap.containsKey(tDimIdentifier))
						GalacticGreg.Logger.error("Found 2 Dimensions with the same Identifier: %s Dimension will not generate Ores", tDimIdentifier);
					else
					{
						boolean tFlag = GregTech_API.sWorldgenFile.get(_mConfigName, getConfigKeyName(mc, mdd), false);
						_mDynWorldConfigMap.put(tDimIdentifier, tFlag);
					}
				}
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
