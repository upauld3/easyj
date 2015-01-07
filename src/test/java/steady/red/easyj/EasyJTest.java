package steady.red.easyj;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EasyJTest {

	@Test
	public void TestHashSet() {
		Set<String> aSet = new HashSet<String>();

		aSet.addAll(Arrays.asList(new String[]{"java"}));

		assertThat(aSet).doesNotContain("");
	}
	
	@Test
	@Parameters({"src/test/resources/test-files/folder-one,java,2", 
				 "src/test/resources/test-files/folder-one,txt,1",
				 "src/test/resources/test-files/non-existent-folder,txt,0"})
	public void FileListerTest(String directoryFilePath, String extension, int fileCount) {
		File rootDirectory = new File(directoryFilePath);
		
		List<String> filesList = EasyJ.getFilePathesListing(rootDirectory, new String[]{extension});
//		EasyJ.println(EasyJ.toListString(filesList));
		
		assertThat(filesList).hasSize(fileCount);
	}

}
