
import java.util.ListIterator;

 public class MyIterator<T>{

        private final ListIterator<T> listIterator;

        private boolean nextWasCalled = false;
        private boolean previousWasCalled = false;

        public MyIterator(ListIterator<T> listIterator) {
            this.listIterator = listIterator;
        }

        public T next() {
            nextWasCalled = true;
            if (previousWasCalled) {
                previousWasCalled = false;
                listIterator.next ();
            }
            return listIterator.next ();
        }

        public T previous() {
            if (nextWasCalled) {
                listIterator.previous();
                nextWasCalled = false;
            }
            previousWasCalled = true;
            return listIterator.previous();
        }
        
        public boolean  hasPrevious () {
            return listIterator.hasPrevious();
        }
        
        public boolean hasNext() {
            return listIterator.hasNext();
        }

    }   