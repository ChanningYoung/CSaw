SCALA_SRC_DIR = src/main/scala
SCALA_TEST_DIR = src/test/scala

TEST_CXX_DIR = src/test/csrc
TEST_RUN_DIR = test_run_dir

RF_SCALA = $(SCALA_SRC_DIR)/core/utils/RegFile.scala $(SCALA_TEST_DIR)/core/RegFileTest.scala

ALU_SCALA = $(SCALA_SRC_DIR)/core/utils/ALU.scala

ALU_VERILOG = $(TEST_RUN_DIR)/ALU.v
ALU_CXX = $(TEST_CXX_DIR)/alu/alu_main.cpp
ALU_OBJ_DIR = $(TEST_RUN_DIR)/alu
ALU_OBJ_MK = VALU.mk
ALU_EXE = alu_sim

.PHONY: regfileTest aluTest clean

regfileTest: $(RF_SCALA)
	sbt 'test:runMain core.RegFileMain'

aluTest: $(ALU_OBJ_DIR)/$(ALU_EXE)
	./$(ALU_OBJ_DIR)/$(ALU_EXE)

$(ALU_OBJ_DIR)/$(ALU_EXE): $(ALU_OBJ_DIR)/$(ALU_OBJ_MK)
	make -C $(@D) -f $(ALU_OBJ_MK)

$(ALU_OBJ_DIR)/$(ALU_OBJ_MK): $(ALU_VERILOG) $(ALU_CXX)
	verilator --cc --exe -o $(ALU_EXE) -Mdir $(ALU_OBJ_DIR) \
		$< $(abspath $(ALU_CXX))

$(ALU_VERILOG): $(ALU_SCALA)
	sbt 'runMain core.utils.AluElaborate -td $(TEST_RUN_DIR)'

clean:
	rm -rf $(TEST_RUN_DIR)
