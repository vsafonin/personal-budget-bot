package ru.vladimir.personalAccounterBot.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;

class ParseLineUtilTest {

	@Test
	void testParse() throws ParseMessageExp {
		String lineForTest = "\"test sda\" 500 20.06.2022";
		assertThat(ParseLineUtil.parse(lineForTest)).size().isEqualTo(3);
		assertThat(ParseLineUtil.parse(lineForTest).get(0)).isEqualTo("test sda");
	}
	@Test
	void testParse2() throws ParseMessageExp {
		String lineForTest = "\"test sda\" \"sds sda\" 500 20.06.2022";
		assertThat(ParseLineUtil.parse(lineForTest)).size().isEqualTo(4);
		assertThat(ParseLineUtil.parse(lineForTest).get(0)).isEqualTo("test sda");
		assertThat(ParseLineUtil.parse(lineForTest).get(1)).isEqualTo("sds sda");
	}
	@Test
	void testParse3() throws ParseMessageExp {
		String lineForTest = "\"test sda\" \"sds sda\"500 20.06.2022";
		List<String> result = ParseLineUtil.parse(lineForTest); 
		assertThat(result).size().isEqualTo(4);
		assertThat(result.get(0)).isEqualTo("test sda");
		assertThat(result.get(1)).isEqualTo("sds sda");
		
	}
	@Test
	void testParse4() throws ParseMessageExp {
		String lineForTest = "\"test sda\"\"sds sda\"500 20.06.2022";
		List<String> result = ParseLineUtil.parse(lineForTest); 
		assertThat(result).size().isEqualTo(4);
		assertThat(result.get(0)).isEqualTo("test sda");
		assertThat(result.get(1)).isEqualTo("sds sda");
		
	}
	@Test
	void testParse5() throws ParseMessageExp {
		String lineForTest = "test sda 500 20.06.2022";
		List<String> result = ParseLineUtil.parse(lineForTest); 
		assertThat(result).size().isEqualTo(4);
		assertThat(result.get(0)).isEqualTo("test");
		assertThat(result.get(1)).isEqualTo("sda");
		
	}

}
