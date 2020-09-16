SCALA_SRC_DIR = src/main/scala

TEST_CXX_DIR = src/test/csrc
TEST_RUN_DIR = test_run_dir

ALU_SCALA = $(SCALA_SRC_DIR)/core/ALU.scala

ALU_VERILOG = $(TEST_RUN_DIR)/ALU.v
ALU_CXX = $(TEST_CXX_DIR)/alu/alu_main.cpp
ALU_OBJ_DIR = $(TEST_RUN_DIR)/alu
ALU_OBJ_MK = VALU.mk
ALU_EXE = alu_sim

.PHONY: aluTest clean

aluTest: $(ALU_OBJ_DIR)/$(ALU_EXE)
	./$(ALU_OBJ_DIR)/$(ALU_EXE)

$(ALU_OBJ_DIR)/$(ALU_EXE): $(ALU_OBJ_DIR)/$(ALU_OBJ_MK)
	make -C $(@D) -f $(ALU_OBJ_MK)

$(ALU_OBJ_DIR)/$(ALU_OBJ_MK): $(ALU_VERILOG) $(ALU_CXX)
	verilator --cc --exe -o $(ALU_EXE) -Mdir $(ALU_OBJ_DIR) \
		$< $(abspath $(ALU_CXX))

$(ALU_VERILOG): $(ALU_SCALA)
	sbt 'runMain core.AluElaborate -td $(TEST_RUN_DIR)'

clean:
	rm -rf $(TEST_RUN_DIR)
