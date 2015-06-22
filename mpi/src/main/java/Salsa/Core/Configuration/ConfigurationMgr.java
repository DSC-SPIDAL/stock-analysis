package Salsa.Core.Configuration;

import Salsa.Core.Configuration.Sections.*;
import Salsa.Core.*;

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [XmlRoot(ElementName = "Configuration")] public class ConfigurationMgr
public class ConfigurationMgr
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	public ConfigurationMgr()
	{
		_global = new GlobalSection();
		_manxcat = new ManxcatSection();
		_pairwise = new PairwiseSection();
		_smithWaterman = new SmithWatermanSection();
		_needlemanWunsch = new NeedlemanWunschSection();
		_smithWatermanMS = new SmithWatermanMS();
		_daVectorSpongeSection = new DAVectorSpongeSection();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private DAVectorSpongeSection _daVectorSpongeSection;
	private GlobalSection _global;
	private ManxcatSection _manxcat;
	private NeedlemanWunschSection _needlemanWunsch;
	private PairwiseSection _pairwise;
	private SmithWatermanSection _smithWaterman;
	private SmithWatermanMS _smithWatermanMS;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

	public final GlobalSection getGlobalSection()
	{
		return _global;
	}
	public final void setGlobalSection(GlobalSection value)
	{
		_global = value;
	}

	public final ManxcatSection getManxcatSection()
	{
		return _manxcat;
	}
	public final void setManxcatSection(ManxcatSection value)
	{
		_manxcat = value;
	}

	public final PairwiseSection getPairwiseSection()
	{
		return _pairwise;
	}
	public final void setPairwiseSection(PairwiseSection value)
	{
		_pairwise = value;
	}

	public final SmithWatermanSection getSmithWatermanSection()
	{
		return _smithWaterman;
	}
	public final void setSmithWatermanSection(SmithWatermanSection value)
	{
		_smithWaterman = value;
	}

	public final SmithWatermanMS getSmithWatermanMS()
	{
		return _smithWatermanMS;
	}
	public final void setSmithWatermanMS(SmithWatermanMS value)
	{
		_smithWatermanMS = value;
	}

	public final NeedlemanWunschSection getNeedlemanWunschSection()
	{
		return _needlemanWunsch;
	}
	public final void setNeedlemanWunschSection(NeedlemanWunschSection value)
	{
		_needlemanWunsch = value;
	}

	public final DAVectorSpongeSection getDAVectorSpongeSection()
	{
		return _daVectorSpongeSection;
	}
	public final void setDAVectorSpongeSection(DAVectorSpongeSection value)
	{
		_daVectorSpongeSection = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public final String[] GetSectionNames()
	{
		return new String[] {"Global", "Manxcat", "Pairwise", "SmithWaterman", "SmithWatermanMS", "NeedlemanWunsch", "DAVectorSponge"};
	}

	public final Section GetSection(String sectionName)
	{
		Section section = null;

//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//		switch (sectionName.ToUpper())
	String tempVar = sectionName.toUpperCase();
//ORIGINAL LINE: case "GLOBAL":
		if (tempVar.equals("GLOBAL"))
		{
		section = _global;
		}
//ORIGINAL LINE: case "PAIRWISE":
		else if (tempVar.equals("PAIRWISE") || tempVar.equals("PWC"))
		{
		section = _pairwise;
		}
//ORIGINAL LINE: case "MANXCAT":
		else if (tempVar.equals("MANXCAT") || tempVar.equals("MDS"))
		{
		section = _manxcat;
		}
//ORIGINAL LINE: case "SMITHWATERMAN":
		else if (tempVar.equals("SMITHWATERMAN") || tempVar.equals("SWG"))
		{
		section = _smithWaterman;
		}
//ORIGINAL LINE: case "SMITHWATERMANMS":
		else if (tempVar.equals("SMITHWATERMANMS") || tempVar.equals("SWMS"))
		{
		section = _smithWatermanMS;
		}
//ORIGINAL LINE: case "NEEDLEMANWUNSCH":
		else if (tempVar.equals("NEEDLEMANWUNSCH") || tempVar.equals("NW"))
		{
		section = _needlemanWunsch;
		}
//ORIGINAL LINE: case "SPONGE":
		else if (tempVar.equals("SPONGE") || tempVar.equals("DAVECTORSPONGE"))
		{
		section = _daVectorSpongeSection;
		}


		return section;
	}

	public final void SaveAs(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			XmlSerializer serializer = new XmlSerializer(ConfigurationMgr.class);
			serializer.Serialize(writer, this);
			writer.Close();
		}
		finally
		{
			writer.dispose();
		}
	}

	public static ConfigurationMgr LoadConfiguration(String fileName, boolean expandMacros)
	{
		ConfigurationMgr manager = null;

//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			XmlSerializer serializer = new XmlSerializer(ConfigurationMgr.class);
			Object tempVar = serializer.Deserialize(reader);
			manager = (ConfigurationMgr)((tempVar instanceof ConfigurationMgr) ? tempVar : null);
			reader.Close();
		}
		finally
		{
			reader.dispose();
		}


		if (expandMacros)
		{
			ExpandMacros(manager);
		}

		return manager;
	}

	private static void ExpandMacros(ConfigurationMgr manager)
	{
		java.util.ArrayList<MacroReplacement> macros = new java.util.ArrayList<MacroReplacement>();
		macros.add(new MacroReplacement("$(ConfigRootPath)", manager.getGlobalSection().getConfigRootPath()));
		macros.add(new MacroReplacement("$(InputRootPath)", manager.getGlobalSection().getInputRootPath()));
		macros.add(new MacroReplacement("$(OutputRootPath)", manager.getGlobalSection().getOutputRootPath()));

		for (String section : manager.GetSectionNames())
		{
			for (MacroReplacement macro : macros)
			{
				Section sec = manager.GetSection(section);
				sec.ExpandMacro(macro);
				sec.ExpandEnvVars();
			}
		}
	}
}