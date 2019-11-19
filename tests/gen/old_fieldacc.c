 private Register getFieldAccessAddress(FieldAccessExpr fae) {
        Register structReg = fae.struct.accept(this);
        StructType struct;
        int offset = 0;

        // struct can be a variable of type struct, or a pointer, or an array
        if (fae.struct instanceof VarExpr) struct = ((StructType) (((VarExpr) fae.struct).vd.type));
        else if (fae.struct instanceof ValueAtExpr) struct = ((StructType) (((ValueAtExpr) fae.struct).type));
        else struct = ((StructType) (((ArrayAccessExpr) fae.struct).type));

        // TODO: what if this is not varexpr? rerun killer, valueat, func.args.c
        // find the offset from the struct for the field
        // struct can be a variable of type struct
//        if (fae.struct instanceof VarExpr) {
//            for (VarDecl vd : struct.std.varDecls) {
//                if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
//            }
//            // or a pointer
//        } else if (fae.struct instanceof ValueAtExpr) {
//            for (VarDecl vd : struct.std.varDecls) {
//                if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
//            }
//            // or an array
//        } else if (fae.struct instanceof ArrayAccessExpr) {
//            for (VarDecl vd : ((StructType) (((ArrayAccessExpr) fae.struct).type)).std.varDecls) {
//                if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
//            }
//        }

        for (VarDecl vd : struct.std.varDecls) {
            if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
        }

        if (fae.struct instanceof VarExpr && ((VarExpr) fae.struct).vd.isGlobal) {
            writer.printf("add %s,%s,%s\n", structReg, structReg, offset);
        }
        // local variables are stored on the stack which grows down so we subtract
        else writer.printf("sub %s,%s,%s\n", structReg, structReg, offset);

        return structReg;
    }