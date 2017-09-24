package study;

import static java.lang.invoke.MethodHandles.lookup;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public class TestInvokeDynamic {
	
	static class ClassA{
		public void println(String s){
			System.out.println(s);
		}
	}
	
	public static void main(String[] args)  throws Throwable{
		Object obj = System.currentTimeMillis()%2 == 0?System.out:new ClassA();
		
		getPrintlnMH(obj);
		
		
	}
	
	private static MethodHandle getPrintlnMH(Object obj) throws Throwable{
		MethodType mt = MethodType.methodType(void.class, String.class);
		
		return lookup().findVirtual(obj.getClass(), "println", mt).bindTo(obj);
	}
}
